package semv.shorturl.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

import semv.shorturl.dto.request.LoginRequest;
import semv.shorturl.dto.request.RegisterRequest;
import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.dto.response.LoginReponse;
import semv.shorturl.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public BaseResponse register(@Valid @RequestBody RegisterRequest r) {
        BaseResponse response = userService.register(r);

        return response;
    }

    @PostMapping("/login")
    public LoginReponse login(@Valid @RequestBody LoginRequest r) {
        LoginReponse response = userService.login(r);

        return response;
    }

    @PostMapping("/logout")
    public BaseResponse logout(@RequestHeader(value = "Authorization") String token) {
        BaseResponse response = userService.logout(token);

        return response;
    }

    @GetMapping("/profile")
    public BaseResponse getProfile(@RequestHeader(value = "Authorization") String token) {
        BaseResponse response = userService.getProfile(token);

        return response;
    }
}
