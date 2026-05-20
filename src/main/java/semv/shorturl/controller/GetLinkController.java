package semv.shorturl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import semv.shorturl.dto.response.GetLinkResponse;
import semv.shorturl.service.ShortLinkService;

import java.net.URI;

@RestController
@RequestMapping("")
public class GetLinkController {
    @Autowired
    private ShortLinkService shortLinkService;

    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        GetLinkResponse response = shortLinkService.getLink(code);

        String originalUrl = response.getOriginalLink();
        return ResponseEntity
                .status(HttpStatus.FOUND) // 302
                .location(URI.create(originalUrl))
                .build();
    }
}