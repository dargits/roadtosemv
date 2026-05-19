package semv.shorturl.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> BaseResponse<T> succes(String mess) { // tra ve thong bao thanh cong nhung khong tra data
        return BaseResponse.<T>builder()
                .code(200)
                .message(mess)
                .build();
    }

    public static <T> BaseResponse<T> succes(String mess, T data) { // tra ve thong bao thanh cong va data
        return BaseResponse.<T>builder()
                .code(200)
                .message(mess)
                .data(data)
                .build();
    }

    public static <T> BaseResponse<T> error(String mess, int code) { // tra ve thong bao loi kem code tu dinh nghia
        return BaseResponse.<T>builder()
                .code(code)
                .message(mess)
                .build();
    }
}
