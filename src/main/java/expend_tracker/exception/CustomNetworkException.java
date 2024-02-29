package expend_tracker.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CustomNetworkException extends RuntimeException {

    private int statusCode;
    private String errorDetails;
    private HttpStatus status;

    public CustomNetworkException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    @Override
    public String toString() {
        return "CustomNetworkException{" +
                "statusCode=" + statusCode +
                ", errorDetails='" + errorDetails + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}

