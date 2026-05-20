package semv.shorturl.service.impl;

import java.security.SecureRandom;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import semv.shorturl.dto.request.CreateLinkRequest;
import semv.shorturl.dto.response.BaseResponse;
import semv.shorturl.dto.response.GetLinkResponse;
import semv.shorturl.entity.ShortLink;
import semv.shorturl.exception.ExistsException;
import semv.shorturl.exception.OverloadException;
import semv.shorturl.repository.ShortLinkRepository;
import semv.shorturl.service.ShortLinkService;

@Service
public class ShortLinkServiceImpl implements ShortLinkService {
    @Value("${system.url}")
    private String systemUrl;
    @Autowired
    private ShortLinkRepository shortLinkRepository;
    final String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public String createShortKey() {
        SecureRandom random = new SecureRandom();
        int count = 0;
        while (true) {
            StringBuilder shortKey = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                int randomIndex = random.nextInt(alpha.length());
                shortKey.append(alpha.charAt(randomIndex));
            }

            String generatedKey = shortKey.toString();
            if (!shortLinkRepository.existsByShortKey(generatedKey)) {
                return generatedKey;
            }
            count++;
            if (count > 10) {
                throw new OverloadException(400, "System busy, please try again.");
            }

        }
    }

    @Override
    public BaseResponse shortLink(CreateLinkRequest r) {
        if (r.getOriginal_link() == null || r.getOriginal_link().trim().isEmpty()) {
            throw new ExistsException(400, "Original URL cannot be empty.");
        }

        String shortKey = createShortKey();

        ShortLink link = ShortLink.builder()
                .originalUrl(r.getOriginal_link().trim())
                .shortKey(shortKey)
                .build();
        shortLinkRepository.save(link);

        return BaseResponse.builder()
                .code(201)
                .message("Short link created successfully.")
                .data(systemUrl + shortKey)
                .build();
    }

    @Override
    public BaseResponse shortLink(CreateLinkRequest r, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'shortLink'");
    }

    @Override
    public GetLinkResponse getLink(String shortKey) {
        if (shortKey.isEmpty() || shortKey.length() < 6) {
            return GetLinkResponse.error("Short key not valid.");
        }
        Optional<ShortLink> tmp = shortLinkRepository.findByShortKey(shortKey);
        if (!tmp.isPresent())
            return GetLinkResponse.error("Short key not found.");

        ShortLink link = tmp.get();

        return GetLinkResponse.succes(link.getOriginalUrl());
    }

}
