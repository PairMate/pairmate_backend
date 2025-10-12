package pairmate.gateway_service.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.cloud.gateway.route.Route;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        ServerHttpRequest decoratedRequest = decorateRequest(exchange, requestId);
        ServerHttpResponse decoratedResponse = decorateResponse(exchange, requestId, startTime);

        return chain.filter(exchange.mutate()
                        .request(decoratedRequest)
                        .response(decoratedResponse)
                        .build())
                .doOnSubscribe(sub -> logPreInfo(exchange, requestId))
                .doFinally(signalType -> log.debug("[REQ-END] requestId={} signal={}", requestId, signalType));
    }

    /** 요청 로깅 */
    private void logPreInfo(ServerWebExchange exchange, String requestId) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().toString();
        String clientIp = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getHostString()
                : "unknown";

        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);

        log.info("[REQ-{}] {} {} from {} route={}",
                requestId, method, path, clientIp, route != null ? route.getId() : "unknown");
    }

    /** 요청 본문 로깅 */
    private ServerHttpRequest decorateRequest(ServerWebExchange exchange, String requestId) {
        ServerHttpRequest request = exchange.getRequest();

        return new ServerHttpRequestDecorator(request) {
            @Override
            public Flux<DataBuffer> getBody() {
                return super.getBody()
                        .publishOn(Schedulers.boundedElastic())
                        .doOnNext(dataBuffer -> {
                            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                                Channels.newChannel(baos).write(dataBuffer.asByteBuffer().asReadOnlyBuffer());
                                String rawBody = baos.toString(StandardCharsets.UTF_8);
                                String body = formatJson(rawBody);
                                log.info("[REQ-BODY-{}] {}", requestId, body);
                            } catch (Exception e) {
                                log.error("[REQ-BODY-{}] Failed to read body: {}", requestId, e.getMessage());
                            }
                        });
            }
        };
    }

    /** 응답 본문 로깅 */
    private ServerHttpResponse decorateResponse(ServerWebExchange exchange, String requestId, long startTime) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory factory = originalResponse.bufferFactory();

        return new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux<?> fluxBody) {
                    return  fluxBody
                            .collectList()
                            .flatMap(dataBuffers -> {
                                DefaultDataBufferFactory joinFactory = new DefaultDataBufferFactory();
                                DataBuffer joined = joinFactory.join((List<? extends DataBuffer>) dataBuffers);
                                byte[] content = new byte[joined.readableByteCount()];
                                joined.read(content);
                                DataBufferUtils.release(joined);

                                String responseBody = new String(content, StandardCharsets.UTF_8);
                                long duration = System.currentTimeMillis() - startTime;
                                int status = getStatusCode().value();

                                log.info("[RES-{}] status={} ({} ms)", requestId, status, duration);
                                log.info("[RES-BODY-{}] {}", requestId, formatJson(responseBody));

                                DataBuffer newBuffer = factory.wrap(content);
                                return getDelegate().writeWith(Mono.just(newBuffer));
                            })
                            .onErrorResume(err -> {
                                log.error("[RES-{}] Error reading body: {}", requestId, err.getMessage());
                                return Mono.empty();
                            });
                }
                return super.writeWith(body);
            }
        };
    }

    /** JSON 공백/포맷 정리 */
    private String formatJson(String json) {
        try {
            JsonNode node = objectMapper.readTree(json);
            return objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException e) {
            return json; // JSON이 아니면 그대로 반환
        }
    }

    @Override
    public int getOrder() {
        // AuthorizationHeaderFilter(-1) 다음 → 0
        return 0;
    }
}