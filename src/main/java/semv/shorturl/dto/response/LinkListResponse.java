package semv.shorturl.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class LinkListResponse {
    private int code;
    private String message;
    private List<LinkInfo> data;

    @Data
    @Builder
    public static class LinkInfo {
        private Long id;
        private String originalUrl;
        private String shortKey;
        private String shortUrl;
        private LocalDateTime createdAt;
    }

    public static LinkListResponse success(List<LinkInfo> links) {
        return LinkListResponse.builder()
                .code(200)
                .message("Get links successfully.")
                .data(links)
                .build();
    }
}
