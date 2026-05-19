package semv.shorturl.dto.request;

import lombok.Data;

@Data
public class RegisterRequest {
    private String account;
    private String password;
    private String userName;
    private String avatarUrl;
}
