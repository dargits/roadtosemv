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
@Table(name = "short_links")
public class ShortLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // Lưu ID của người tạo (có thể null nếu cho phép khách tạo ẩn danh)

    @Column(name = "original_url", nullable = false, columnDefinition = "TEXT")
    private String originalUrl; // Dùng kiểu TEXT để chứa được các link gốc siêu dài

    @Column(name = "short_key", nullable = false, unique = true, length = 10)
    private String shortKey; // Mã rút gọn duy nhất (ví dụ: g8X2a)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}