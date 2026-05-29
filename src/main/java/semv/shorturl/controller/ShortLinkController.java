package semv.shorturl.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import semv.shorturl.dto.request.CreateLinkRequest;
import semv.shorturl.dto.response.AnalyticsResponse;
import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.dto.response.LinkListResponse;
import semv.shorturl.service.RedisService;
import semv.shorturl.service.ShortLinkService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1/links")
public class ShortLinkController {
    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    private RedisService redisService;

    @PostMapping("/")
    public BaseResponse shortlink(
            @Valid @RequestBody CreateLinkRequest r,
            @RequestHeader(value = "Authorization", required = false) String token) {

        // Kiểm tra có token trong header không
        if (token != null && !token.trim().isEmpty()) {
            // Lấy thông tin user từ Redis
            String[] tokenData = redisService.getToken(token);

            // Nếu token hợp lệ, gọi hàm có userId
            if (tokenData != null && tokenData.length >= 2) {
                Long userId = Long.parseLong(tokenData[0]);
                return shortLinkService.shortLink(r, userId);
            }
        }

        // Nếu không có token hoặc token không hợp lệ, tạo link ẩn danh
        return shortLinkService.shortLink(r);
    }

    @GetMapping("/my-links")
    public LinkListResponse getMyLinks(@RequestHeader(value = "Authorization") String token) {
        // Lấy thông tin user từ Redis
        String[] tokenData = redisService.getToken(token);

        if (tokenData == null || tokenData.length < 2) {
            return LinkListResponse.builder()
                    .code(401)
                    .message("Unauthorized. Please login.")
                    .build();
        }

        Long userId = Long.parseLong(tokenData[0]);
        return shortLinkService.getUserLinks(userId);
    }

    @GetMapping("/analytics/{linkId}")
    public AnalyticsResponse getLinkAnalytics(
            @PathVariable Long linkId,
            @RequestHeader(value = "Authorization") String token) {
        // Lấy thông tin user từ Redis
        String[] tokenData = redisService.getToken(token);

        if (tokenData == null || tokenData.length < 2) {
            return AnalyticsResponse.error("Unauthorized. Please login.", 401);
        }

        Long userId = Long.parseLong(tokenData[0]);
        return shortLinkService.getLinkAnalytics(linkId, userId);
    }

    @GetMapping("/delete/{linkId}")
    public BaseResponse deleteLink(
            @PathVariable Long linkId,
            @RequestHeader(value = "Authorization") String token) {
        String[] tokenData = redisService.getToken(token);

        if (tokenData == null || tokenData.length < 2) {
            return BaseResponse.error("Unauthorized. Please login.", 401);
        }

        Long userId = Long.parseLong(tokenData[0]);
        return shortLinkService.deleteLink(linkId, userId);
    }

    @PostMapping("/update/{linkId}")
    public BaseResponse updateLink(
            @PathVariable Long linkId,
            @Valid @RequestBody CreateLinkRequest r,
            @RequestHeader(value = "Authorization") String token) {
        String[] tokenData = redisService.getToken(token);

        if (tokenData == null || tokenData.length < 2) {
            return BaseResponse.error("Unauthorized. Please login.", 401);
        }

        Long userId = Long.parseLong(tokenData[0]);
        return shortLinkService.updateLink(linkId, r, userId);
    }

}
