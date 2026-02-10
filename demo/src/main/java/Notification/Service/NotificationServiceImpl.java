package Notification.Service;

import Notification.Dto.Response.NotificationListResponse;
import Notification.Dto.Response.NotificationResponse;
import Notification.Entity.NotificationChannel;
import Notification.Repository.NotificationRepository;
import com.sun.nio.sctp.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationListResponse getMyNotifications(String userId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize); // Spring page bắt đầu từ 0

        // Sử dụng Promise.all trong TS -> Chạy song song hoặc tuần tự trong Java
        List<Notification.Entity.Notification> items = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        long total = notificationRepository.countByUserId(userId);
        long unread = notificationRepository.countByUserIdAndReadAtIsNull(userId);

        List<NotificationResponse> data = items.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return NotificationListResponse.builder()
                .data(data)
                .total(total)
                .page(page)
                .pageSize(pageSize)
                .unread(unread)
                .build();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(String id, String userId) {
        Notification.Entity.Notification notif = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notif.getUserId().equals(userId)) {
            throw new RuntimeException("Notification not found"); // Giả lập 404 để bảo mật
        }

        notif.setReadAt(LocalDateTime.now());
        Notification.Entity.Notification updated = notificationRepository.save(notif);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {
        notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForAdmin(String type) {
        Pageable limit = PageRequest.of(0, 100); // Take 100
        List<Notification.Entity.Notification> items;

        if (type != null && !type.isEmpty()) {
            items = notificationRepository.findByTypeOrderByCreatedAtDesc(type, limit);
        } else {
            items = notificationRepository.findAllByOrderByCreatedAtDesc(limit);
        }

        return items.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createNotification(String userId, String title, String body, String type, Map<String, Object> metadata) {
        // Tạo dedupKey để tránh trùng lặp nếu cần thiết (hoặc dùng UUID random)
        // Ở đây dùng UUID random + timestamp để đảm bảo unique đơn giản
        String dedupKey = UUID.randomUUID().toString();

        Notification.Entity.Notification notification = Notification.Entity.Notification.builder()
                .userId(userId)
                .title(title)
                .body(body)
                .type(type != null ? type : "SYSTEM") // Default type
                .metadata(metadata)
                .channel(NotificationChannel.IN_APP) // Default channel
                .dedupKey(dedupKey)
                .build();

        notificationRepository.save(notification);

        // TODO: Nếu có tích hợp Realtime (WebSocket/FCM), bắn event tại đây
        // realtimeService.sendToUser(userId, notification);
    }

    @Override
    @Transactional
    public void createNotification(String userId, String body) {
        // Gọi hàm đầy đủ với các giá trị mặc định
        createNotification(userId, "Thông báo mới", body, "SYSTEM", null);
    }

    private NotificationResponse mapToResponse(Notification.Entity.Notification entity) {
        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .type(entity.getType())
                .title(entity.getTitle())
                .body(entity.getBody())
                .metadata(entity.getMetadata())
                .channel(entity.getChannel())
                .readAt(entity.getReadAt())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
