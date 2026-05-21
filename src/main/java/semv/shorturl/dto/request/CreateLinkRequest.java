package semv.shorturl.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLinkRequest {
    @NotBlank(message = "Original link is required")
    @Pattern(regexp = "^https?://.*", message = "Original link must be a valid URL starting with http:// or https://")
    private String original_link;

    @Size(min = 6, max = 10, message = "Custom link must be between 6 and 10 characters")
    private String custom_link;
}
