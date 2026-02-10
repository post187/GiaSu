package Notification.Dto.Response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

import Notification.Entity.NotificationChannel;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class NotificationResponse {
    private String id;
    private String userId;
    private String type;
    private String title;
    private String body;
    private Map<String, Object> metadata;
    private NotificationChannel channel;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}