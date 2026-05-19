package semv.shorturl.exception;

import lombok.Data;

@Data
public class OverloadException extends RuntimeException {
    private int code;
    private String mess;

    public OverloadException(int code, String mess) {
        super(mess);
        this.code = code;
    }

}
