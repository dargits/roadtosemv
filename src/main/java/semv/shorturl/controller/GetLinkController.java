package semv.shorturl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import semv.shorturl.dto.response.GetLinkResponse;
import semv.shorturl.service.ShortLinkService;

import java.net.URI;

@RestController
@RequestMapping("")
public class GetLinkController {
    @Autowired
    private ShortLinkService shortLinkService;

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code, HttpServletRequest request) {
        // Lấy IP address từ request
        String ipAddress = getClientIp(request);

        // Gọi service với tracking
        GetLinkResponse response = shortLinkService.getLink(code, ipAddress);

        String originalUrl = response.getOriginalLink();

        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity.notFound().build(); // 404
        }
        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .location(URI.create(originalUrl))
                .build();
    }

    // Helper method để lấy IP thực của client (xử lý cả proxy/load balancer)
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Nếu có nhiều IP (qua nhiều proxy), lấy IP đầu tiên
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}