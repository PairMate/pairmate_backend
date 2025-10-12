package pairmate.gateway_service.response;

import org.springframework.http.HttpStatusCode;

public interface BaseCode {
    HttpStatusCode getHttpStatus();
    String getCode();
    String getMessage();
}