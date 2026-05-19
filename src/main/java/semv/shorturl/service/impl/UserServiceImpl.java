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
}
