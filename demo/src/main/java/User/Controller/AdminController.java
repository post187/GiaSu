package User.Controller;

import Config.APIResponse;
import User.DTO.Request.ClassFilterRequest;
import User.DTO.Request.TutorRejectRequest;
import User.DTO.Response.ClassAdminResponse;
import User.DTO.Response.TutorPendingResponse;
import User.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController  {
    @Autowired
    private AdminService adminService;

    @GetMapping("/tutors/pending")
    public ResponseEntity<APIResponse<List<TutorPendingResponse>>> getPendingTutors() {
        return ResponseEntity.ok(APIResponse.success(adminService.getPendingTutors()));
    }

    @PostMapping("/tutors/{id}/approve")
    public ResponseEntity<APIResponse<?>> approveTutor(@PathVariable String id) {
        adminService.approveTutor(id);
        return ResponseEntity.ok(APIResponse.success("Approve success"));
    }

    @PostMapping("/tutors/{id}/reject")
    public ResponseEntity<APIResponse<?>> rejectTutor(@PathVariable String id, @RequestBody TutorRejectRequest request) {
        adminService.rejectTutor(id, request.getNote());
        return ResponseEntity.ok(APIResponse.success("Reject success"));
    }

    @GetMapping("/classes")
    public ResponseEntity<APIResponse<List<ClassAdminResponse>>> getClasses(ClassFilterRequest filter) {
        return ResponseEntity.ok(APIResponse.success(adminService.getClasses(filter)));
    }
}
