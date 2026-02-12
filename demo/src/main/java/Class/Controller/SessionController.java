package Class.Controller;

import Class.Dto.Request.SessionActionRequest;
import Class.Dto.Response.SessionResponse;
import Class.Service.SessionService;
import Config.APIResponse;
import User.DTO.Response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    private String getCurrentUserId() {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    /**
     * PATCH /api/sessions/:id/start
     * Tutor hoặc Student xác nhận bắt đầu buổi học
     */
    @PatchMapping("/{id}/start")
    @PreAuthorize("hasAnyAuthority('TUTOR', 'STUDENT')")
    public ResponseEntity<APIResponse<SessionResponse>> startSession(
            @PathVariable String id,
            @RequestBody(required = false) SessionActionRequest request) { // Body rỗng
        try {
            return ResponseEntity.ok(APIResponse.success(sessionService.startSession(id, getCurrentUserId())));
        } catch (RuntimeException e) {
            if ("Session not found".equals(e.getMessage())) return ResponseEntity.notFound().build();
            if ("Forbidden: Not a participant".equals(e.getMessage())) return ResponseEntity.status(403).build();
            return ResponseEntity.badRequest().build(); // handle other errors
        }
    }

    /**
     * PATCH /api/sessions/:id/complete
     * Tutor hoặc Student xác nhận kết thúc buổi học
     */
    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyAuthority('TUTOR', 'STUDENT')")
    public ResponseEntity<APIResponse<SessionResponse>> completeSession(
            @PathVariable String id,
            @RequestBody(required = false) SessionActionRequest request) {
        try {
            return ResponseEntity.ok(APIResponse.success(sessionService.completeSession(id, getCurrentUserId())));
        } catch (RuntimeException e) {
            if ("Session not found".equals(e.getMessage())) return ResponseEntity.notFound().build();
            if ("Forbidden: Not a participant".equals(e.getMessage())) return ResponseEntity.status(403).build();
            return ResponseEntity.badRequest().build();
        }
    }
}