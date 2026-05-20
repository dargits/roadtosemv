package semv.shorturl.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetLinkResponse {
    private int code;
    private boolean status;
    private String originalLink;
    private String mess;

    public static GetLinkResponse succes(String originalLink) {
        return GetLinkResponse.builder()
                .status(true)
                .code(200)
                .mess("Get original link success.")
                .originalLink(originalLink)
                .build();
    }

    public static GetLinkResponse error(String mess) {
        return GetLinkResponse.builder()
                .status(false)
                .mess(mess)
                .code(400)
                .build();
    }
}
