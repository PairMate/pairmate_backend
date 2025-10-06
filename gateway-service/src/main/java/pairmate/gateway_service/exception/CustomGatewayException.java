package pairmate.gateway_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomGatewayException extends RuntimeException {

    private final HttpStatus status;

    public CustomGatewayException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
}