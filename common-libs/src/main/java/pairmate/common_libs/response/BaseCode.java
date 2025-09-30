package pairmate.common_libs.response;

// import org.apache.http.HttpStatus;
import org.springframework.http.HttpStatus;


public interface BaseCode {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}