package semv.shorturl.exception;

import lombok.Data;

@Data
public class ExistsException extends RuntimeException {
    private final int code;

    public ExistsException(int code, String message) {
        super(message);
        this.code = code;
    }
}
