package Notification.Entity;

import User.Entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notif_user_created", columnList = "userId, createdAt"),
        @Index(name = "idx_notif_dedup", columnList = "userId, dedupKey", unique = true)
})
@Data

public class Notification {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    // Hibernate 6 hỗ trợ JSON mapping native.
    // Nếu dùng bản cũ hơn cần thư viện ngoài (hypersistence-utils) hoặc Converter.
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationChannel channel = NotificationChannel.IN_APP;

    @Column(nullable = false)
    private String dedupKey;

    private LocalDateTime readAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;
}