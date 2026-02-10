package Notification.Repository;

import Notification.Entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    // Đếm tổng số notif của user
    long countByUserId(String userId);

    // Đếm số notif chưa đọc
    long countByUserIdAndReadAtIsNull(String userId);

    // Filter cho Admin (theo type)
    List<Notification> findByTypeOrderByCreatedAtDesc(String type, Pageable pageable);

    // Admin lấy tất cả
    List<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Mark all as read
    @Modifying
    @Query("UPDATE Notification n SET n.readAt = :now WHERE n.userId = :userId AND n.readAt IS NULL")
    void markAllAsRead(@Param("userId") String userId, @Param("now") LocalDateTime now);
}
