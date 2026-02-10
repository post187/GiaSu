package Notification.Service;

import Notification.Dto.Response.NotificationListResponse;
import Notification.Dto.Response.NotificationResponse;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    NotificationListResponse getMyNotifications(String userId, int page, int pageSize);
    NotificationResponse markAsRead(String id, String userId);
    void markAllAsRead(String userId);
    void createNotification(String userId, String title, String body, String type, Map<String, Object> metadata);
    void createNotification(String userId, String body);
    // Admin
    List<NotificationResponse> getNotificationsForAdmin(String type);
}
