package semv.shorturl.exception;

import lombok.Data;

@Data
public class NotFoundException extends RuntimeException {
    private int code;
    private String mess;

    public NotFoundException(int code, String mess) {
        super(mess);
        this.code = code;
    }

}
