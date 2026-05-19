package semv.shorturl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import semv.shorturl.dto.request.CreateLinkRequest;
import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.service.ShortLinkService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/links")
public class ShortLinkController {
    @Autowired
    private ShortLinkService shortLinkService;

    @PostMapping("/")
    public BaseResponse shortlink(@RequestBody CreateLinkRequest r) {
        BaseResponse response = shortLinkService.shortLink(r);

        return response;
    }

}
