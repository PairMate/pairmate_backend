package pairmate.gateway_service.exception;

import org.springframework.web.client.HttpStatusCodeException;

import javax.lang.model.type.ErrorType;

public class AuthException extends HttpStatusCodeException {

    public AuthException(ErrorType errorType) {
        super(errorType.getStatusCode(), errorType.getMessage());
    }

    @Override
    public String getMessage() {
        return getStatusText();
    }
}