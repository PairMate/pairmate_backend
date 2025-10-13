package pairmate.store_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.ErrorCode;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 서비스 전반에서 발생하는 CustomException을 처리
     * .orElseThrow(() -> new CustomException(...)) 구문에서 던져진 예외를 여기서 잡기
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        ErrorCode errorCode = (ErrorCode) e.getErrorCode();
        String message = e.getMessage();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.onFailure(errorCode, message));
    }
}