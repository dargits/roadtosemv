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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Khai báo ID tự tăng (Auto Increment) trong MySQL
    private Long id;

    @Column(name = "account", nullable = false, unique = true, length = 50)
    private String account;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "user_name", length = 100)
    private String userName;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Column(name = "is_locked", nullable = false)
    @Builder.Default
    private Boolean isLocked = false;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "role", length = 20)
    @Builder.Default
    private String role = "USER";
}