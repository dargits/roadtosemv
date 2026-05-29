package semv.shorturl.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import semv.shorturl.dto.request.LoginRequest;
import semv.shorturl.dto.request.RegisterRequest;
import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.dto.response.LoginReponse;
import semv.shorturl.entity.User;
import semv.shorturl.exception.ExistsException;
import semv.shorturl.repository.UserRepository;
import semv.shorturl.service.RedisService;
import semv.shorturl.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RedisService redisService;
    private BCryptPasswordEncoder hash = new BCryptPasswordEncoder();

    @Override
    public BaseResponse register(RegisterRequest r) {
        if (userRepository.existsByAccount(r.getAccount())) {
            throw new ExistsException(400, "Account is exists.");
        }
        User newUser = User.builder()
                .account(r.getAccount())
                .userName(r.getUserName())
                .password(hash.encode(r.getPassword()))
                .avatarUrl(r.getAvatarUrl())
                .build();

        userRepository.save(newUser);

        return BaseResponse.builder()
                .code(201)
                .message("Register success, please sign in.")
                .build();
    }

    @Override
    public LoginReponse login(LoginRequest r) {
        Optional<User> tmp = userRepository.findByAccount(r.getAccount());

        if (!tmp.isPresent()) {
            throw new ExistsException(400, "Account or password incorrect.");
        }

        User user = tmp.get();

        if (!hash.matches(r.getPassword(), user.getPassword())) {
            throw new ExistsException(400, "Account or password incorrect.");
        }

        // Xóa token cũ nếu có
        String oldToken = redisService.get("user_token:" + user.getId());
        if (oldToken != null) {
            redisService.delete("token:" + oldToken);
            redisService.delete("user_token:" + user.getId());
        }

        // Lưu token mới
        String token = UUID.randomUUID().toString();
        redisService.saveToken(token, user.getId(), user.getRole());
        redisService.set("user_token:" + user.getId(), token, 1 * 24 * 3600); // 1 ngày

        return LoginReponse.succes(token);
    }

    @Override
    public BaseResponse logout(String token) {
        String[] tokenData = redisService.getToken(token);

        if (tokenData == null || tokenData.length < 2) {
            throw new ExistsException(401, "Invalid token.");
        }

        Long userId = Long.parseLong(tokenData[0]);

        // Xóa token
        redisService.delete("token:" + token);
        redisService.delete("user_token:" + userId);

        return BaseResponse.builder()
                .code(200)
                .message("Logout successfully.")
                .build();
    }

    @Override
    public BaseResponse getProfile(String token) {
        String[] tokenData = redisService.getToken(token);

        if (tokenData == null || tokenData.length < 2) {
            throw new ExistsException(401, "Invalid token.");
        }

        Long userId = Long.parseLong(tokenData[0]);
        Optional<User> tmp = userRepository.findById(userId);

        if (!tmp.isPresent()) {
            throw new ExistsException(404, "User not found.");
        }

        User user = tmp.get();

        var profileData = new java.util.HashMap<String, Object>();
        profileData.put("id", user.getId());
        profileData.put("account", user.getAccount());
        profileData.put("userName", user.getUserName());
        profileData.put("avatarUrl", user.getAvatarUrl());
        profileData.put("role", user.getRole());
        profileData.put("createdAt", user.getCreatedAt());

        return BaseResponse.builder()
                .code(200)
                .message("Get profile successfully.")
                .data(profileData)
                .build();
    }
}
