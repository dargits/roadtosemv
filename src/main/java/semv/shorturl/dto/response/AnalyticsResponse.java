package semv.shorturl.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AnalyticsResponse {
    private int code;
    private String message;
    private AnalyticsData data;

    @Data
    @Builder
    public static class AnalyticsData {
        private Long linkId;
        private String shortKey;
        private String originalUrl;
        private Long totalClicks;
        private List<ClickInfo> recentClicks;
    }

    @Data
    @Builder
    public static class ClickInfo {
        private String ipAddress;
        private String country;
        private String status;
        private LocalDateTime clickedAt;
    }

    public static AnalyticsResponse success(AnalyticsData data) {
        return AnalyticsResponse.builder()
                .code(200)
                .message("Get analytics successfully.")
                .data(data)
                .build();
    }

    public static AnalyticsResponse error(String message, int code) {
        return AnalyticsResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
}
