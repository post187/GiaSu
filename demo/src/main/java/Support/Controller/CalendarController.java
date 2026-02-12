package Support.Controller;

import Config.APIResponse;
import Support.Dto.Response.CalendarItemResponse;
import Support.Service.CalendarService;
import User.DTO.Response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/calendar")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    private String getCurrentUserId() {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    /**
     * GET /api/calendar/tutor
     * Lấy lịch dạy cho gia sư
     */
    @GetMapping("/tutor")
    @PreAuthorize("hasAuthority('TUTOR')")
    public ResponseEntity<APIResponse<List<CalendarItemResponse>>> getTutorCalendar() {
        try {
            return ResponseEntity.ok(APIResponse.success(calendarService.getTutorCalendar(getCurrentUserId())));
        } catch (RuntimeException e) {
            if ("Tutor profile not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }

    /**
     * GET /api/calendar/student
     * Lấy lịch học cho học sinh
     */
    @GetMapping("/student")
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<APIResponse<List<CalendarItemResponse>>> getStudentCalendar() {
        try {
            return ResponseEntity.ok(APIResponse.success(calendarService.getStudentCalendar(getCurrentUserId())));
        } catch (RuntimeException e) {
            if ("Student profile not found".equals(e.getMessage())) {
                return ResponseEntity.notFound().build();
            }
            throw e;
        }
    }
}