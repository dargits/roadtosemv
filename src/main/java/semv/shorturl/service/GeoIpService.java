package semv.shorturl.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class GeoIpService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getCountryFromIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return "Unknown";
        }

        // Bỏ qua IP local
        if (ipAddress.startsWith("127.") || ipAddress.startsWith("192.168.") ||
                ipAddress.startsWith("10.") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
            return "Local";
        }

        try {
            String url = "https://ipwho.is/" + ipAddress;
            String response = restTemplate.getForObject(url, String.class);

            if (response != null) {
                JsonNode root = objectMapper.readTree(response);

                // Kiểm tra success = true
                if (root.has("success") && root.get("success").asBoolean()) {
                    if (root.has("country")) {
                        return root.get("country").asText();
                    }
                }
            }

            return "Unknown";

        } catch (Exception e) {
            log.error("Failed to get country for IP: " + ipAddress, e);
            return "Unknown";
        }
    }
}
