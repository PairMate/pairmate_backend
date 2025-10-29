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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import pairmate.gateway_service.exception.CustomException;
import pairmate.gateway_service.response.ApiResponse;
import pairmate.gateway_service.response.ErrorCode;
import pairmate.gateway_service.jwt.JwtProvider;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Gateway 전역 인증 필터
 * AccessToken 검증 → X-User-Id / X-User-Role 헤더 추가
 */
@Component
@Slf4j
@Order(-1)
@RequiredArgsConstructor
public class AuthorizationHeaderFilter implements GlobalFilter, Ordered {

    private final JwtProvider jwtProvider;
    private final WebClient.Builder webClientBuilder;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final List<String> EXCLUDED_PATHS = List.of(
            // Swagger 관련
            "/swagger-ui.html",
            "/swagger-ui/",
            "/v3/api-docs",
            "/webjars/",
            "/api/user-service/swagger-ui.html",
            "/api/user-service/swagger-ui/",
            "/api/user-service/v3/api-docs",
            "/api/user-service/webjars/",
            "/api/store-service/swagger-ui.html",
            "/api/store-service/swagger-ui/",
            "/api/store-service/v3/api-docs",
            "/api/store-service/webjars/",
            "/api/review-service/swagger-ui.html",
            "/api/review-service/swagger-ui/",
            "/api/review-service/v3/api-docs",
            "/api/review-service/webjars/",
            "/api/pay-service/swagger-ui.html",
            "/api/pay-service/swagger-ui/",
            "/api/pay-service/v3/api-docs",
            "/api/pay-service/webjars/",

            // Auth 관련
            "/api/auth/login",
            "/api/auth/signup",
            "/api/auth/reissue"
    );


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        log.info("[AUTH] Path: {}", path);

        // 인증 제외 경로 통과
        log.info("[AUTH] Incoming request path: {}", path);
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            log.info("[AUTH] Skipping auth for path: {}", path);
            return chain.filter(exchange);
        }

        // Authorization 헤더 추출
        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("[AUTH] Authorization header missing or invalid format");
            return onError(exchange, new CustomException(ErrorCode.TOKEN_MISSING));
        }

        String accessToken = authorizationHeader.substring(7);

        try {
            // 토큰 검증
            if (!jwtProvider.validateToken(accessToken)) {
                return onError(exchange, new CustomException(ErrorCode.INVALID_TOKEN));
            }

            // Claims 추출
            Claims claims = jwtProvider.parseClaims(accessToken);
            Long userId = Long.valueOf(claims.getSubject());
            String role = claims.get("role", String.class);

            log.info("[AUTH] User authenticated -> userId={}, role={}", userId, role);

            // 유저 정보 헤더 추가 후 요청 전달
            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Role", role != null ? role : "USER")
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (ExpiredJwtException e) {
            log.info("[TOKEN] AccessToken expired, attempting reissue...");
            return handleReissue(exchange, chain, accessToken);
        } catch (JwtException e) {
            log.warn("[TOKEN] Invalid JWT: {}", e.getMessage());
            return onError(exchange, new CustomException(ErrorCode.INVALID_TOKEN));
        } catch (Exception e) {
            log.error("[TOKEN] Unexpected error: {}", e.getMessage());
            return onError(exchange, new CustomException(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * AccessToken 만료 시 user-service의 /reissue 요청 수행
     */
    private Mono<Void> handleReissue(ServerWebExchange exchange, GatewayFilterChain chain, String accessToken) {
        return webClientBuilder.build()
                .post()
                .uri("lb://USER-SERVICE/auth/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("accessToken", accessToken))
                .retrieve()
                .bodyToMono(Map.class)
                .flatMap(res -> {
                    Map<String, Object> body = (Map<String, Object>) res.get("data");
                    String newAccessToken = (String) body.get("accessToken");
                    if (newAccessToken == null) {
                        return onError(exchange, new CustomException(ErrorCode.TOKEN_REISSUE_FAILED));
                    }

                    // 새 토큰 파싱 및 헤더 갱신
                    Claims claims = jwtProvider.parseClaims(newAccessToken);
                    Long userId = Long.valueOf(claims.getSubject());
                    String role = claims.get("role", String.class);

                    ServerHttpRequest newRequest = exchange.getRequest().mutate()
                            .header("Authorization", "Bearer " + newAccessToken)
                            .header("X-User-Id", String.valueOf(userId))
                            .header("X-User-Role", role != null ? role : "USER")
                            .build();

                    log.info("[TOKEN] AccessToken reissued successfully for userId={}", userId);
                    return chain.filter(exchange.mutate().request(newRequest).build());
                })
                .onErrorResume(e -> {
                    log.error("[TOKEN] Reissue failed: {}", e.getMessage());
                    return onError(exchange, new CustomException(ErrorCode.INVALID_REFRESH_TOKEN));
                });
    }

    /**
     * WebFlux 에러 처리
     */
    private Mono<Void> onError(ServerWebExchange exchange, CustomException e) {
        ApiResponse<Object> body = ApiResponse.onFailure(e.getErrorCode(), e.getMessage());
        byte[] bytes = body.toString().getBytes(StandardCharsets.UTF_8);
        var buffer = exchange.getResponse().bufferFactory().wrap(bytes);

        exchange.getResponse().setStatusCode(e.getErrorCode().getHttpStatus());
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().getHeaders().setContentLength(bytes.length);

        log.warn("[GATEWAY ERROR] {}", e.getErrorCode().getMessage());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
