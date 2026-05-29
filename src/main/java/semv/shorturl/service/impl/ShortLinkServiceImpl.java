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
import semv.shorturl.entity.LinkAnalytics;
import semv.shorturl.entity.ShortLink;
import semv.shorturl.exception.ExistsException;
import semv.shorturl.exception.OverloadException;
import semv.shorturl.repository.LinkAnalysticsRepository;
import semv.shorturl.repository.ShortLinkRepository;
import semv.shorturl.service.ShortLinkService;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {

    @Value("${system.url}")
    private String systemUrl;

    @Autowired
    private ShortLinkRepository shortLinkRepository;

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
                shortKey.append(alpha.charAt(random.nextInt(alpha.length())));
            }

            String generatedKey = shortKey.toString();
            if (!shortLinkRepository.existsByShortKey(generatedKey)) {
                return generatedKey;
            }
            if (++count > 10) {
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

        if (r.getCustom_link() != null && !r.getCustom_link().isEmpty()) {
            if (shortLinkRepository.existsByShortKey(r.getCustom_link()))
                return BaseResponse.error("Custom link exists.", 400);
            shortKey = r.getCustom_link();
        }

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

        return GetLinkResponse.succes(tmp.get().getOriginalUrl());
    }

    @Override
    public GetLinkResponse getLink(String shortKey, String ipAddress) {
        if (shortKey.isEmpty() || shortKey.length() < 6) {
            return GetLinkResponse.error("Short key not valid.");
        }

        Optional<ShortLink> tmp = shortLinkRepository.findByShortKey(shortKey);
        if (!tmp.isPresent()) {
            trackClick(null, ipAddress, "FAILED");
            return GetLinkResponse.error("Short key not found.");
        }

        ShortLink link = tmp.get();
        trackClick(link.getId(), ipAddress, "SUCCESS");

        return GetLinkResponse.succes(link.getOriginalUrl());
    }

    // Tracking với GeoIP
    @Autowired
    private semv.shorturl.service.GeoIpService geoIpService;

    private void trackClick(Long linkId, String ipAddress, String status) {
        try {
            String country = geoIpService.getCountryFromIp(ipAddress);

            LinkAnalytics analytics = LinkAnalytics.builder()
                    .linkId(linkId)
                    .ipAddress(ipAddress)
                    .country(country)
                    .status(status)
                    .clickedAt(LocalDateTime.now())
                    .build();

            linkAnalyticsRepository.save(analytics);
        } catch (Exception e) {
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
        Optional<ShortLink> tmp = shortLinkRepository.findById(linkId);
        if (!tmp.isPresent()) {
            return AnalyticsResponse.error("Link not found.", 404);
        }

        ShortLink link = tmp.get();

        if (link.getUserId() == null || !link.getUserId().equals(userId)) {
            return AnalyticsResponse.error("You don't have permission to view this analytics.", 403);
        }

        Long totalClicks = linkAnalyticsRepository.countByLinkId(linkId);

        var recentAnalytics = linkAnalyticsRepository.findTop20ByLinkIdOrderByClickedAtDesc(linkId);

        var clickInfos = recentAnalytics.stream()
                .map(a -> AnalyticsResponse.ClickInfo.builder()
                        .ipAddress(a.getIpAddress())
                        .country(a.getCountry())
                        .status(a.getStatus())
                        .clickedAt(a.getClickedAt())
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

    @Override
    public BaseResponse deleteLink(Long linkId, Long userId) {
        Optional<ShortLink> tmp = shortLinkRepository.findById(linkId);
        if (!tmp.isPresent()) {
            return BaseResponse.error("Link not found.", 404);
        }

        ShortLink link = tmp.get();

        if (link.getUserId() == null || !link.getUserId().equals(userId)) {
            return BaseResponse.error("You don't have permission to delete this link.", 403);
        }

        shortLinkRepository.delete(link);

        return BaseResponse.builder()
                .code(200)
                .message("Link deleted successfully.")
                .build();
    }

    @Override
    public BaseResponse updateLink(Long linkId, CreateLinkRequest r, Long userId) {
        Optional<ShortLink> tmp = shortLinkRepository.findById(linkId);
        if (!tmp.isPresent()) {
            return BaseResponse.error("Link not found.", 404);
        }

        ShortLink link = tmp.get();

        if (link.getUserId() == null || !link.getUserId().equals(userId)) {
            return BaseResponse.error("You don't have permission to update this link.", 403);
        }

        // Cập nhật original URL
        if (r.getOriginal_link() != null && !r.getOriginal_link().trim().isEmpty()) {
            link.setOriginalUrl(r.getOriginal_link().trim());
        }

        // Cập nhật custom link nếu có
        if (r.getCustom_link() != null && !r.getCustom_link().isEmpty()) {
            if (!r.getCustom_link().equals(link.getShortKey())) {
                if (shortLinkRepository.existsByShortKey(r.getCustom_link())) {
                    return BaseResponse.error("Custom link already exists.", 400);
                }
                link.setShortKey(r.getCustom_link());
            }
        }

        shortLinkRepository.save(link);

        return BaseResponse.builder()
                .code(200)
                .message("Link updated successfully.")
                .data(systemUrl + link.getShortKey())
                .build();
    }
}