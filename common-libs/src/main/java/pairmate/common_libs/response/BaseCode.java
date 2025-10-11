package pairmate.common_libs.response;

import org.springframework.http.HttpStatusCode;

public interface BaseCode {
    HttpStatusCode getHttpStatus();
    String getCode();
    String getMessage();
}