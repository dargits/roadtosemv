package semv.shorturl.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import semv.shorturl.dto.request.CreateLinkRequest;
import semv.shorturl.dto.response.AnalyticsResponse;
import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.dto.response.GetLinkResponse;
import semv.shorturl.dto.response.LinkListResponse;
import semv.shorturl.entity.ShortLink;
import semv.shorturl.exception.ExistsException;
import semv.shorturl.exception.OverloadException;
import semv.shorturl.repository.LinkAnalysticsRepository;
import semv.shorturl.repository.ShortLinkRepository;
import semv.shorturl.service.RedisService;
import semv.shorturl.service.ShortLinkService;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {
    @Value("${system.url}")
    private String systemUrl;
    @Autowired
    private ShortLinkRepository shortLinkRepository;
    @Autowired
    private RedisService redisService;
    @Autowired
    private LinkAnalysticsRepository linkAnalyticsRepository;
    final String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public String createShortKey() {
        SecureRandom random = new SecureRandom();
        int count = 0;
        while (true) {
            StringBuilder shortKey = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                int randomIndex = random.nextInt(alpha.length());
                shortKey.append(alpha.charAt(randomIndex));
            }

            String generatedKey = shortKey.toString();
            if (!shortLinkRepository.existsByShortKey(generatedKey)) {
                return generatedKey;
            }
            count++;
            if (count > 10) {
                throw new OverloadException(400, "System busy, please try again.");
            }

        }
    }

    @Override
    public BaseResponse shortLink(CreateLinkRequest r) {
        if (r.getOriginal_link() == null || r.getOriginal_link().trim().isEmpty()) {
            throw new ExistsException(400, "Original URL cannot be empty.");
        }

        String shortKey = createShortKey();

        ShortLink link = ShortLink.builder()
                .originalUrl(r.getOriginal_link().trim())
                .shortKey(shortKey)
                .createdAt(LocalDateTime.now())
                .build();
        shortLinkRepository.save(link);

        return BaseResponse.builder()
                .code(201)
                .message("Short link created successfully.")
                .data(systemUrl + shortKey)
                .build();
    }

    @Override
    public BaseResponse shortLink(CreateLinkRequest r, Long userId) {
        if (r.getOriginal_link() == null || r.getOriginal_link().trim().isEmpty()) {
            throw new ExistsException(400, "Original URL cannot be empty.");
        }

        String shortKey = createShortKey();

        ShortLink link = ShortLink.builder()
                .originalUrl(r.getOriginal_link().trim())
                .shortKey(shortKey)
                .createdAt(LocalDateTime.now())
                .userId(userId)
                .build();
        shortLinkRepository.save(link);

        return BaseResponse.builder()
                .code(201)
                .message("Short link created successfully.")
                .data(systemUrl + shortKey)
                .build();
    }

    @Override
    public GetLinkResponse getLink(String shortKey) {
        if (shortKey.isEmpty() || shortKey.length() < 6) {
            return GetLinkResponse.error("Short key not valid.");
        }
        Optional<ShortLink> tmp = shortLinkRepository.findByShortKey(shortKey);
        if (!tmp.isPresent())
            return GetLinkResponse.error("Short key not found.");

        ShortLink link = tmp.get();

        return GetLinkResponse.succes(link.getOriginalUrl());
    }

    @Override
    public GetLinkResponse getLink(String shortKey, String ipAddress) {
        if (shortKey.isEmpty() || shortKey.length() < 6) {
            return GetLinkResponse.error("Short key not valid.");
        }

        Optional<ShortLink> tmp = shortLinkRepository.findByShortKey(shortKey);
        if (!tmp.isPresent()) {
            // Tracking FAILED click (không có linkId vì không tìm thấy)
            trackClick(null, ipAddress, "FAILED");
            return GetLinkResponse.error("Short key not found.");
        }

        ShortLink link = tmp.get();

        // Tracking SUCCESS click với linkId
        trackClick(link.getId(), ipAddress, "SUCCESS");

        return GetLinkResponse.succes(link.getOriginalUrl());
    }

    // Helper method để tracking click vào Redis
    private void trackClick(Long linkId, String ipAddress, String status) {
        try {
            // Format: linkId|timestamp|ip|status
            // Ví dụ: "123|2026-05-20T10:30:45|192.168.1.1|SUCCESS"
            String payload = (linkId != null ? linkId : "null") + "|" +
                    LocalDateTime.now() + "|" +
                    ipAddress + "|" +
                    status;

            // Lưu vào Redis List với key tracking:linkId (hoặc tracking:failed nếu không có linkId)
            String redisKey = linkId != null ? linkId.toString() : "failed";
            redisService.pushClick(redisKey, payload);
        } catch (Exception e) {
            // Log lỗi nhưng không làm gián đoạn redirect
            System.err.println("Failed to track click: " + e.getMessage());
        }
    }

    @Override
    public LinkListResponse getUserLinks(Long userId) {
        var links = shortLinkRepository.findByUserIdOrderByCreatedAtDesc(userId);

        var linkInfos = links.stream()
                .map(link -> LinkListResponse.LinkInfo.builder()
                        .id(link.getId())
                        .originalUrl(link.getOriginalUrl())
                        .shortKey(link.getShortKey())
                        .shortUrl(systemUrl + link.getShortKey())
                        .createdAt(link.getCreatedAt())
                        .build())
                .toList();

        return LinkListResponse.success(linkInfos);
    }

    @Override
    public AnalyticsResponse getLinkAnalytics(Long linkId, Long userId) {
        // Kiểm tra link có tồn tại không
        Optional<ShortLink> tmp = shortLinkRepository.findById(linkId);
        if (!tmp.isPresent()) {
            return AnalyticsResponse.error("Link not found.", 404);
        }

        ShortLink link = tmp.get();

        // Kiểm tra quyền sở hữu
        if (link.getUserId() == null || !link.getUserId().equals(userId)) {
            return AnalyticsResponse.error("You don't have permission to view this analytics.", 403);
        }

        // Lấy tổng số clicks
        Long totalClicks = linkAnalyticsRepository.countByLinkId(linkId);

        // Lấy 20 clicks gần nhất
        var recentAnalytics = linkAnalyticsRepository.findTop20ByLinkIdOrderByClickedAtDesc(linkId);

        var clickInfos = recentAnalytics.stream()
                .map(analytics -> AnalyticsResponse.ClickInfo.builder()
                        .ipAddress(analytics.getIpAddress())
                        .country(analytics.getCountry())
                        .status(analytics.getStatus())
                        .clickedAt(analytics.getClickedAt())
                        .build())
                .toList();

        var analyticsData = AnalyticsResponse.AnalyticsData.builder()
                .linkId(link.getId())
                .shortKey(link.getShortKey())
                .originalUrl(link.getOriginalUrl())
                .totalClicks(totalClicks)
                .recentClicks(clickInfos)
                .build();

        return AnalyticsResponse.success(analyticsData);
    }

}

