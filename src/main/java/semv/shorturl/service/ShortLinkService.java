package semv.shorturl.service;

import semv.shorturl.dto.request.CreateLinkRequest;
import semv.shorturl.dto.response.BaseResponse;

public interface ShortLinkService {
    public String createShortKey();

    public BaseResponse shortLink(CreateLinkRequest r);

    public BaseResponse shortLink(CreateLinkRequest r, Long userId);
}
