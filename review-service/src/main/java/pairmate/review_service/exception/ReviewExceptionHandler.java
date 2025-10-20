package pairmate.review_service.exception;

import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.builder()
                .isSuccess(false)
                .code(ErrorCode.NOT_FOUND.getCode())
                .message(e.getMessage())
                .result(null)
                .build();
    }

    /**
     * 리뷰 수정/삭제 권한이 없을 때 발생
     */
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> handleIllegalStateException(IllegalStateException e) {
        return ApiResponse.builder()
                .isSuccess(false)
                .code(ErrorCode.FORBIDDEN.getCode())
                .message(e.getMessage())
                .result(null)
                .build();
    }

    /**
     * Feign 클라이언트 호출 시 발생하는 예외 처리
     * 다른 서비스랑 연결할라구 호출할 때 (ex: review에서 storeId 같은거)
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Object>> handleFeignException(FeignException e) {
        // FeignException에서 HTTP 상태 코드를 가져오는 부분이에요
        HttpStatus status = HttpStatus.valueOf(e.status());
        String errorMessage;

        // 애초에 storeId가 없는 거인 경우
        if (status == HttpStatus.NOT_FOUND) {
            errorMessage = "요청한 가게(Store) 정보를 찾을 수 없습니다.";
            return ResponseEntity.status(status).body(ApiResponse.onFailure(ErrorCode.NOT_FOUND, errorMessage));
        }

        // 그 외의 경우 (그냥 뭉뚱그려..)
        errorMessage = "외부 서비스 호출 중 오류가 발생했습니다.";
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR, errorMessage));
    }

    /**
     * CustomException을 처리하는 핸들러
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {

        ErrorCode errorCode = (ErrorCode) e.getErrorCode();

        // 2. 표준 ApiResponse 에러 본문을 만듭니다.
        ApiResponse<Object> body = ApiResponse.builder()
                .isSuccess(false)
                .code(errorCode.getCode())
                .message(e.getMessage())
                .result(null)
                .build();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(body);
    }
}