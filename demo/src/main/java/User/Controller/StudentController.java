package User.Controller;

import Config.APIResponse;
import User.DTO.Request.StudentUpdateRequest;
import User.DTO.Response.StudentProfileResponse;
import User.Service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/students")
@PreAuthorize("hasRole('STUDENT')") // Chỉ học sinh mới được vào
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/me")
    public ResponseEntity<APIResponse<StudentProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(APIResponse.success(studentService.getMyProfile()));
    }

    @PatchMapping("/me")
    public ResponseEntity<APIResponse<StudentProfileResponse>> updateProfile(
            @RequestBody StudentUpdateRequest dto) {
        return ResponseEntity.ok(APIResponse.success(studentService.updateProfile( dto)));
    }
}
