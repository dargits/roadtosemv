package semv.shorturl.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginReponse {
    private int code;
    private String message;
    private String token;

    public static LoginReponse succes(String token) {
        return LoginReponse.builder()
                .code(200)
                .message("Login succes.")
                .token(token)
                .build();
    }

    public static LoginReponse fail() {
        return LoginReponse.builder()
                .code(400)
                .message("Login fail.")
                .build();
    }

}
