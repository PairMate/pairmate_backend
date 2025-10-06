package pairmate.common_libs.exception;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pairmate.common_libs.response.ApiResponse;
import pairmate.common_libs.response.ErrorCode;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    // @RequestParam, @PathVariable 등 Bean Validation 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException e, WebRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .findFirst()
                .orElse("잘못된 요청입니다.");

        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorCode.INVALID_REQUEST,
                message
        );
        return handleExceptionInternal(e, body, new HttpHeaders(),
                ErrorCode.INVALID_REQUEST.getHttpStatus(),
                request);
    }

    // @Valid @RequestBody DTO 검증 실패
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            String msg = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
            errors.merge(field, msg, (a, b) -> a + ", " + b);
        });

        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorCode.INVALID_REQUEST,
                errors
        );
        return handleExceptionInternal(e, body, headers,
                ErrorCode.INVALID_REQUEST.getHttpStatus(),
                request);
    }

    // 모든 미처리 예외 → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnknownException(Exception e, WebRequest request) {
        log.error("Unhandled exception", e);
        ApiResponse<Object> body = ApiResponse.onFailure(ErrorCode.INTERNAL_SERVER_ERROR, null);
        return handleExceptionInternal(e, body, new HttpHeaders(),
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus(), request);
    }

    // 도메인 CustomException
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Object> handleCustomException(CustomException e, HttpServletRequest request) {
        ApiResponse<Object> body = ApiResponse.onFailure(e.getErrorCode(), null);
        WebRequest webRequest = new ServletWebRequest(request);
        return handleExceptionInternal(e, body, new HttpHeaders(), e.getErrorCode().getHttpStatus(), webRequest);
    }

    @ExceptionHandler({InvalidFormatException.class})
    public ResponseEntity<ApiResponse<Object>> handleInvalidDateFormat(InvalidFormatException ex) {
        if (ex.getTargetType() == LocalDate.class) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.onFailure(ErrorCode.INVALID_DATE, null)
            );
        }
        return ResponseEntity.badRequest().body(
                ApiResponse.onFailure(ErrorCode.INVALID_FORMAT, null)
        );
    }

}
