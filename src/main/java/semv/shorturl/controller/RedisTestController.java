package semv.shorturl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/redis-test")
public class RedisTestController {

    @Autowired
    private StringRedisTemplate redisTemplate; // Thư viện lõi có sẵn của Spring, không lo lỗi import

    @GetMapping("/run")
    public String testRedisCloud() {
        StringBuilder result = new StringBuilder("<h3>=== KẾT QUẢ TEST REDIS CLOUD ===</h3>");

        try {
            // 1. Test ghi một Key đơn giản lên Render
            String testKey = "semv:test:message";
            redisTemplate.opsForValue().set(testKey, "Hello Redis Cloud from Spring Boot 2026!");
            result.append("<p style='color: green;'>1. Ghi dữ liệu lên Redis: THÀNH CÔNG!</p>");

            // 2. Đọc ngược dữ liệu từ Render về máy
            String value = redisTemplate.opsForValue().get(testKey);
            result.append("<p>-> Dữ liệu đọc về: <b>").append(value).append("</b></p>");

            // 3. Test đẩy vào hàng đợi (List)
            String listKey = "semv:test:queue";
            redisTemplate.opsForList().rightPush(listKey, "Log Click 1");
            redisTemplate.opsForList().rightPush(listKey, "Log Click 2");
            result.append("<p style='color: green;'>2. Đẩy 2 phần tử vào hàng đợi List: THÀNH CÔNG!</p>");

            // 4. Kiểm tra kích thước hàng đợi hiện tại
            Long size = redisTemplate.opsForList().size(listKey);
            result.append("<p>-> Số lượng phần tử trong hàng đợi: <b>").append(size).append("</b></p>");

            // Dọn dẹp rác sau khi test xong
            redisTemplate.delete(testKey);
            redisTemplate.delete(listKey);

            result.append("<h4 style='color: blue;'>=> KẾT LUẬN: KẾT NỐI ĐẾN RENDER HOÀN HẢO!</h4>");

        } catch (Exception e) {
            result.append("<p style='color: red;'> XẢY RA LỖI KẾT NỐI: ").append(e.getMessage()).append("</p>");
            e.printStackTrace();
        }

        return result.toString();
    }
}