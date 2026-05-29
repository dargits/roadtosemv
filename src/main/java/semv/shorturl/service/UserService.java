package semv.shorturl.service;

import semv.shorturl.dto.request.LoginRequest;
import semv.shorturl.dto.request.RegisterRequest;
import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.dto.response.LoginReponse;

public interface UserService {
    public BaseResponse register(RegisterRequest r);

    public LoginReponse login(LoginRequest r);

    public BaseResponse logout(String token);

    public BaseResponse getProfile(String token);
}
