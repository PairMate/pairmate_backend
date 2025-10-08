package pairmate.gateway_service.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.ErrorCode;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.*;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("[Global Error Handler] {}", ex.getMessage(), ex);

        ApiResponse<Object> body;
        int status;

        // CustomException
        if (ex instanceof CustomException customEx) {
            status = customEx.getErrorCode().getHttpStatus().value();
            body = ApiResponse.onFailure(customEx.getErrorCode(), null);

            // JWT 관련 예외
        } else if (ex instanceof ExpiredJwtException) {
            status = ErrorCode.EXPIRED_ACCESS_TOKEN.getHttpStatus().value();
            body = ApiResponse.onFailure(ErrorCode.EXPIRED_ACCESS_TOKEN, null);

        } else if (ex instanceof SignatureException) {
            status = ErrorCode.TOKEN_SIGNATURE_INVALID.getHttpStatus().value();
            body = ApiResponse.onFailure(ErrorCode.TOKEN_SIGNATURE_INVALID, null);

        } else if (ex instanceof MalformedJwtException) {
            status = ErrorCode.INVALID_TOKEN.getHttpStatus().value();
            body = ApiResponse.onFailure(ErrorCode.INVALID_TOKEN, null);

        } else if (ex instanceof UnsupportedJwtException) {
            status = ErrorCode.UNSUPPORTED_TOKEN.getHttpStatus().value();
            body = ApiResponse.onFailure(ErrorCode.UNSUPPORTED_TOKEN, null);

        } else if (ex instanceof IllegalArgumentException) {
            status = ErrorCode.TOKEN_MISSING.getHttpStatus().value();
            body = ApiResponse.onFailure(ErrorCode.TOKEN_MISSING, null);

            // 기타 예외
        } else {
            status = ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value();
            body = ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR, null);
        }

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (Exception e) {
            log.error("JSON 직렬화 실패: {}", e.getMessage());
            bytes = ("{\"isSuccess\":false,\"code\":\"COMMON-500\",\"message\":\"JSON 직렬화 실패\"}")
                    .getBytes(StandardCharsets.UTF_8);
            status = 500;
        }

        // 응답 설정
        var response = exchange.getResponse();
        response.setStatusCode(org.springframework.http.HttpStatus.valueOf(status));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.getHeaders().setContentLength(bytes.length);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }
}