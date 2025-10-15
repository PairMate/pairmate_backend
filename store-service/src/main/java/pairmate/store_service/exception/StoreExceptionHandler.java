package pairmate.store_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pairmate.common_libs.exception.CustomException;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.ErrorCode;

@RestControllerAdvice
public class StoreExceptionHandler {

    // "store not found" 와 같은 예외 처리
    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<Object> handleCustomException(CustomException e) {
        return ApiResponse.builder()
                .isSuccess(false)
                .code(e.getErrorCode().getCode())
                .message(e.getMessage())
                .result(null)
                .build();
    }

    // "카테고리 없음" 과 같은 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return ApiResponse.builder()
                .isSuccess(false)
                .code(ErrorCode.INVALID_REQUEST.getCode())
                .message(e.getMessage())
                .result(null)
                .build();
    }
}