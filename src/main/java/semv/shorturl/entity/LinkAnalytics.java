package semv.shorturl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "link_analytics")
public class LinkAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link_id", nullable = false)
    private Long linkId; // Kết nối tới ID của bảng short_links

    @Column(name = "ip_address", length = 45)
    private String ipAddress; // Độ dài 45 ký tự để chứa được cả IPv4 và IPv6

    @Column(name = "country", length = 100)
    private String country; // Quốc gia phân tích từ IP (Ví dụ: Vietnam, United States)

    @Column(name = "status", length = 20)
    private String status; // Trạng thái click ("SUCCESS" hoặc "FAILED")

    @Column(name = "clicked_at")
    private LocalDateTime clickedAt; // Thời điểm click thực tế (Lấy từ lúc ghi vào Redis)
}