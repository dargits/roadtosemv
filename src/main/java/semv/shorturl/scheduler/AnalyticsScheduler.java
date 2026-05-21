package semv.shorturl.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import semv.shorturl.entity.LinkAnalytics;
import semv.shorturl.repository.LinkAnalysticsRepository;
import semv.shorturl.service.GeoIpService;
import semv.shorturl.service.RedisService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
public class AnalyticsScheduler {

    @Autowired
    private RedisService redisService;

    @Autowired
    private LinkAnalysticsRepository linkAnalyticsRepository;

    @Autowired
    private GeoIpService geoIpService;

    @Scheduled(fixedRate = 300000)
    public void syncAnalyticsToDatabase() {
        log.info("Starting analytics sync job...");

        try {
            Set<String> trackingKeys = redisService.getAllTrackingKeys();

            if (trackingKeys == null || trackingKeys.isEmpty()) {
                log.info("No tracking data found in Redis");
                return;
            }

            int totalSaved = 0;

            for (String key : trackingKeys) {
                String linkIdStr = key.replace("tracking:", "");
                List<String> clicks = redisService.popAllClicks(linkIdStr);

                if (clicks.isEmpty()) {
                    continue;
                }

                List<LinkAnalytics> analyticsList = new ArrayList<>();

                for (String payload : clicks) {
                    try {
                        LinkAnalytics analytics = parsePayload(payload);
                        if (analytics != null) {
                            analyticsList.add(analytics);
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse payload: " + payload, e);
                    }
                }

                if (!analyticsList.isEmpty()) {
                    linkAnalyticsRepository.saveAll(analyticsList);
                    totalSaved += analyticsList.size();
                }
            }

            log.info("Analytics sync completed. Saved {} records", totalSaved);

        } catch (Exception e) {
            log.error("Error during analytics sync", e);
        }
    }

    private LinkAnalytics parsePayload(String payload) {
        String[] parts = payload.split("\\|");

        if (parts.length < 4) {
            log.warn("Invalid payload format: " + payload);
            return null;
        }

        try {
            Long linkId = "null".equals(parts[0]) ? null : Long.parseLong(parts[0]);
            LocalDateTime clickedAt = LocalDateTime.parse(parts[1]);
            String ipAddress = parts[2];
            String status = parts[3];

            // Lấy country từ IP
            String country = geoIpService.getCountryFromIp(ipAddress);

            return LinkAnalytics.builder()
                    .linkId(linkId)
                    .clickedAt(clickedAt)
                    .ipAddress(ipAddress)
                    .status(status)
                    .country(country)
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse payload parts: " + payload, e);
            return null;
        }
    }
}
