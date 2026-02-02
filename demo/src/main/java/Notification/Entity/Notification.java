package Notification.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private String type;
    private String title;
    private String body;
    @Column(columnDefinition = "jsonb") private String metadata;
    @Enumerated(EnumType.STRING) private NotificationChannel channel = NotificationChannel.IN_APP;
    private String dedupKey;
    private LocalDateTime readAt;
    @CreationTimestamp
    private LocalDateTime createdAt;
}