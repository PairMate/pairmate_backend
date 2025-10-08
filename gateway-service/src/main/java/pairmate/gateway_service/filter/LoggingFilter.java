package pairmate.gateway_service.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        String method = request.getMethod().toString();
        String clientIp = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getHostString()
                : "unknown";

        log.info("[REQ-{}] {} {} from {}", requestId, method, path, clientIp);

        // 요청 통과 후 응답 시점에서 로깅
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            int statusCode = exchange.getResponse().getStatusCode() != null
                    ? exchange.getResponse().getStatusCode().value()
                    : 0;

            log.info("[RES-{}] {} {} ({} ms, status={})",
                    requestId, method, path, duration, statusCode);
        }));
    }

    @Override
    public int getOrder() {
        // 인증 필터 다음에 실행
        return 0;
    }
}
