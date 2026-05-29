package semv.shorturl.exception.handel;

import org.springframework.web.bind.MethodArgumentNotValidException;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handelValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation failed");
        return BaseResponse.error(message, 400);
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handelGeneral(Exception ex) {
        return BaseResponse.error("Internal server error: " + ex.getMessage(), 500);
    }
}