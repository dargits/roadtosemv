package semv.shorturl.exception.handel;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.exception.ExistsException;
import semv.shorturl.exception.NotFoundException;
import semv.shorturl.exception.OverloadException;

@RestControllerAdvice
public class GlobalHandelException {

    @ExceptionHandler(ExistsException.class)
    public BaseResponse<?> handel(ExistsException ex) {
        return BaseResponse.error(ex.getMessage(), ex.getCode());
    }

    @ExceptionHandler(OverloadException.class)
    public BaseResponse<?> handelOverLoad(OverloadException ex) {
        return BaseResponse.error(ex.getMessage(), ex.getCode());
    }

    @ExceptionHandler(NotFoundException.class)
    public BaseResponse<?> handelNotFound(NotFoundException ex) {
        return BaseResponse.error(ex.getMessage(), ex.getCode());
    }
}