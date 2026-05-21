package semv.shorturl.service;

import semv.shorturl.dto.request.CreateLinkRequest;
import semv.shorturl.dto.response.AnalyticsResponse;
import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.dto.response.GetLinkResponse;
import semv.shorturl.dto.response.LinkListResponse;

public interface ShortLinkService {
    public String createShortKey();

    public BaseResponse shortLink(CreateLinkRequest r);

    public BaseResponse shortLink(CreateLinkRequest r, Long userId);

    public GetLinkResponse getLink(String shortKey);

    public GetLinkResponse getLink(String shortKey, String ipAddress);

    public LinkListResponse getUserLinks(Long userId);

    public AnalyticsResponse getLinkAnalytics(Long linkId, Long userId);

}
