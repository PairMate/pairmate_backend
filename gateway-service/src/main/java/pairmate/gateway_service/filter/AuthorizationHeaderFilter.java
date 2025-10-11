package pairmate.gateway_service.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.ErrorCode;
import pairmate.gateway_service.jwt.JwtProvider;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Gateway 전역 인증 필터
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthorizationHeaderFilter implements GlobalFilter, Ordered {

    private final JwtProvider jwtProvider;

    @Value("${jwt.secret}")
    private String secretKey;

    // user-service 호출을 위한 webclient
    private final WebClient.Builder webClientBuilder;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/reissue"
    );



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        // 인증 제외 경로 통과
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        }
        // 헤더 추출, 예외 처리
        String AuthorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (AuthorizationHeader != null && AuthorizationHeader.startsWith("Bearer ")) {
            return onError(exchange,new CustomException(ErrorCode.TOKEN_MISSING));
        }

        String accessToken = AuthorizationHeader.substring(7);
        try {
            // 엑세스 토큰 검증
            if (!jwtProvider.validateToken(accessToken)) {
                return onError(exchange,new CustomException(ErrorCode.INVALID_TOKEN));
            }
            // claims 추출
            Claims claims = jwtProvider.parseClaims(accessToken);
            Long userId = Long.valueOf(claims.getSubject());

            // 헤더에 유저 정보를 추가 후, 다음 체인으로 전달
            ServerHttpRequest request = exchange.getRequest().mutate().header("Authorization", accessToken).build();
            return chain.filter(exchange.mutate().request(request).build());
        } catch (ExpiredJwtException e) {
            log.info("[TOKEN] AccessToken 만료, 재발급 시도");
            return handleReissue(exchange, chain, accessToken);
        }
        catch (JwtException e) {
            log.warn("[TOKEN] Invalid JWT: {}", e.getMessage());
            return onError(exchange, new CustomException(ErrorCode.INVALID_TOKEN));
        } catch (Exception e) {
            log.error("[TOKEN] Unexpected error: {}", e.getMessage());
            return onError(exchange, new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    // 엑세스 토큰 만료 시 user-service의 /reissue 요청 전달
    private Mono<Void> handleReissue(ServerWebExchange exchange, GatewayFilterChain chain, String accessToken) {
        return webClientBuilder.build()
                .post()
                .uri("http://user-service:8081/api/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("accessToken", accessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(res -> {
                    Map<String, Object> body = (Map<String, Object>) res.get("data");
                    String newAccessToken = (String) body.get("accessToken");
                    if (accessToken == null) {
                        return onError(exchange,new CustomException(ErrorCode.TOKEN_REISSUE_FAILED));
                    }

                    // 발급받은 토큰으로 헤더 갱신
                    ServerHttpRequest newRequest = exchange.getRequest().mutate()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + newAccessToken)
                            .build();

                    log.info("[TOKEN] AccessToken reissued successfully");
                    return chain.filter(exchange.mutate().request(newRequest).build());
                })
                .onErrorResume(e -> {
                    log.error("[TOKEN] Reissue failed: {}", e.getMessage());
                    return onError(exchange, new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
                });
    }

    // spring webflux 에러처리
    private Mono<Void> onError(ServerWebExchange exchange, CustomException e) {
        ApiResponse<Object> body = ApiResponse.onFailure(e.getErrorCode(), e.getMessage());

        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        exchange.getResponse().setStatusCode(e.getErrorCode().getHttpStatus());
        exchange.getResponse().getHeaders().setContentLength(bytes.length);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        log.warn("[GATEWAY ERROR] {}", e.getErrorCode().getMessage());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    // 라우팅 전에 토큰 검증 실행
    @Override
    public int getOrder() {
        return -1;
    }
}
