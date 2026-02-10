package Class.Controller;

import Booking.Dto.Response.BookingResponse;
import Class.Dto.Request.*;
import Class.Dto.Response.ClassDetailResponse;
import Class.Dto.Response.ClassResponse;
import Class.Dto.Response.ClassScheduleResponse;
import Class.Entity.ClassStatus;
import Class.Service.ClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
@Slf4j
public class ClassController {

    private final ClassService classService;

    // --- HELPER: Lấy User ID từ Token ---
    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new RuntimeException("User not authenticated");
        }
        // Tùy vào cấu hình Security (JWT), principal có thể là String (userId) hoặc UserDetails
        return auth.getName();
    }

    // --- HELPER: Lấy User Role (nếu cần cho logic view sessions) ---
    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Logic lấy role string từ authorities (Ví dụ: "ROLE_TUTOR" -> "TUTOR")
        return auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("STUDENT");
    }

    // ================== PUBLIC ENDPOINTS ==================

    // 1. GET /api/classes (List & Filter)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClassResponse>> getClasses(@ModelAttribute ClassFilterRequest filter) {
        return ResponseEntity.ok(classService.getClasses(filter));
    }

    // 2. GET /api/classes/{id} (Public details)
    @GetMapping("/{id}")
    public ResponseEntity<ClassResponse> getClassById(@PathVariable String id) {
        return ResponseEntity.ok(classService.getClassById(id));
    }

    // 3. GET /api/classes/{id}/schedule (Public schedule config)
    @GetMapping("/{id}/schedule")
    public ResponseEntity<ClassScheduleResponse> getClassSchedule(@PathVariable String id) {
        return ResponseEntity.ok(classService.getClassSchedule(id));
    }

    // ================== PROTECTED ENDPOINTS (TUTOR/ADMIN) ==================

    // 4. POST /api/classes (Create Class)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('TUTOR')")
    public ResponseEntity<ClassResponse> createClass(@Valid @RequestBody ClassRequest request) {
        String userId = getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(classService.createClass(userId, request));
    }

    // 5. PATCH /api/classes/{id} (Update Class Details)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TUTOR')")
    public ResponseEntity<ClassResponse> updateClass(
            @PathVariable String id,
            @Valid @RequestBody UpdateClassRequest request) {
        return ResponseEntity.ok(classService.updateClass(id, getCurrentUserId(), request));
    }

    // 6. PATCH /api/classes/{id}/status (Publish/Draft/Archive)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('TUTOR', 'ADMIN')")
    public ResponseEntity<ClassResponse> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) { // Dùng Map để lấy field status nhanh

        String statusStr = body.get("status");
        if (statusStr == null) {
            throw new RuntimeException("Status is required");
        }
        ClassStatus status = ClassStatus.valueOf(statusStr);

        return ResponseEntity.ok(classService.updateClassStatus(id, getCurrentUserId(), status));
    }

    // 7. PATCH /api/classes/{id}/cancel (Cancel Class)
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyAuthority('TUTOR', 'ADMIN')")
    public ResponseEntity<Void> cancelClass(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {

        String reason = body.get("reason");
        classService.cancelClass(id, getCurrentUserId(), reason);
        return ResponseEntity.ok().build();
    }

    // 8. DELETE /api/classes/{id} (Soft Delete)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('TUTOR', 'ADMIN')")
    public ResponseEntity<Void> deleteClass(@PathVariable String id) {
        classService.deleteClass(id, getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    // ================== MANAGEMENT ENDPOINTS ==================

    // 9. GET /api/classes/{id}/students (List confirmed students)
    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyAuthority('TUTOR', 'ADMIN')")
    public ResponseEntity<List<BookingResponse>> getClassStudents(@PathVariable String id) {
        return ResponseEntity.ok(classService.getClassStudents(id, getCurrentUserId()));
    }

    // 10. GET /api/classes/{id}/sessions (View Sessions - Check quyền bên trong NotificationService)
    @GetMapping("/{id}/sessions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClassDetailResponse> getClassSessions(@PathVariable String id) {
        // NotificationService sẽ tự check: Nếu là Tutor Owner hoặc Student đã Booking thì mới trả về
        return ResponseEntity.ok(classService.getClassSessions(id, getCurrentUserId(), getCurrentUserRole()));
    }

    // 11. POST /api/classes/{id}/schedule (Update Schedule & Generate Sessions)
    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasAnyAuthority('TUTOR', 'ADMIN')")
    public ResponseEntity<ClassScheduleResponse> updateSchedule(
            @PathVariable String id,
            @Valid @RequestBody ScheduleRequest request) {
        return ResponseEntity.ok(classService.updateSchedule(id, getCurrentUserId(), request));
    }

    // 12. DELETE /api/classes/{id}/schedule (Clear Schedule)
    @DeleteMapping("/{id}/schedule")
    @PreAuthorize("hasAnyAuthority('TUTOR', 'ADMIN')")
    public ResponseEntity<Void> clearSchedule(@PathVariable String id) {
        classService.clearSchedule(id, getCurrentUserId());
        return ResponseEntity.ok().build();
    }
}