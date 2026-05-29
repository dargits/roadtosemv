package semv.shorturl.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redis;

    // -------- STRING --------

    public void set(String key, String value, long ttlSeconds) {
        redis.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return redis.opsForValue().get(key);
    }

    public void delete(String key) {
        redis.delete(key);
    }

    public boolean exists(String key) {
        return Boolean.TRUE.equals(redis.hasKey(key));
    }

    // -------- TOKEN SESSION --------

    public void saveToken(String token, Long userId, String role) {
        // Lưu dạng "userId:role" cho đơn giản
        redis.opsForValue().set("token:" + token, userId + ":" + role, 7, TimeUnit.DAYS);
    }

    public String[] getToken(String token) {
        String val = redis.opsForValue().get("token:" + token);
        if (val == null)
            return null;
        return val.split(":"); // [0] = userId, [1] = role
    }

    public void deleteToken(String token) {
        redis.delete("token:" + token);
    }
}