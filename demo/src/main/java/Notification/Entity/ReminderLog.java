package Notification.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
public class ReminderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private String sessionId;
    @Enumerated(EnumType.STRING) private ReminderType reminderType;
    @CreationTimestamp
    private LocalDateTime sentAt;
}
