package Notification.Controller;

import Config.APIResponse;
import Notification.Dto.Response.NotificationListResponse;
import Notification.Dto.Response.NotificationResponse;
import Notification.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // Helper: Lấy UserID từ Security Context
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * GET /api/notifications/me
     * Lấy danh sách thông báo của user hiện tại
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // authGuard()
    public ResponseEntity<APIResponse<NotificationListResponse>> getMyNotifications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize
    ) {
        // Limit max pageSize giống logic TS (max 50)
        int safePageSize = Math.min(pageSize, 50);
        return ResponseEntity.ok(APIResponse.success(
                notificationService.getMyNotifications(getCurrentUserId(), page, safePageSize)
        ));
    }

    /**
     * PATCH /api/notifications/:id/read
     * Đánh dấu 1 thông báo là đã đọc
     */
    @PatchMapping("/{id}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<NotificationResponse>> markRead(@PathVariable String id) {
        try {
            return ResponseEntity.ok(APIResponse.success(notificationService.markAsRead(id, getCurrentUserId())));
        } catch (RuntimeException e) {
            if ("Notification not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    /**
     * PATCH /api/notifications/read-all
     * Đánh dấu tất cả là đã đọc
     */
    @PatchMapping("/read-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<APIResponse<Map<String, String>>> markAllRead() {
        notificationService.markAllAsRead(getCurrentUserId());
        return ResponseEntity.ok(APIResponse.success(Map.of("message", "OK")));
    }

    /**
     * GET /api/notifications
     * Admin: Lấy danh sách thông báo (có filter type)
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')") // authGuard([UserRole.ADMIN])
    public ResponseEntity<APIResponse<List<NotificationResponse>>> getNotifications(
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(APIResponse.success(notificationService.getNotificationsForAdmin(type)));
    }
}