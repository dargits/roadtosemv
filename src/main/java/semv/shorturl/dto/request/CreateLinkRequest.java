package semv.shorturl.dto.request;

import lombok.Data;

@Data
public class CreateLinkRequest {
    private String original_link;
    private String custom_link;
}
