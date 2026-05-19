package semv.shorturl.exception.handel;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.exception.ExistsException;
import semv.shorturl.exception.OverloadException;

@RestControllerAdvice
public class GlobalHandelException {

    @ExceptionHandler(ExistsException.class)
    public BaseResponse<?> handel(ExistsException ex) {
        // ex.getMessage() lấy từ super(message) của RuntimeException
        // ex.getCode() lấy từ thuộc tính code của ExistsException nhờ @Data
        return BaseResponse.error(ex.getMessage(), ex.getCode());
    }

    @ExceptionHandler(OverloadException.class)
    public BaseResponse<?> handelOverLoad(OverloadException ex) {
        return BaseResponse.error(ex.getMessage(), ex.getCode());
    }
}