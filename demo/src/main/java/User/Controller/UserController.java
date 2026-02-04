package User.Controller;

import Config.APIResponse;
import User.DTO.Response.UserResponse;
import User.Service.UserService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserController {

    @Autowired
    UserService userService;

    // 1. Lấy thông tin cá nhân (Tương ứng GET /me)
    @GetMapping("/me")
    public ResponseEntity<APIResponse<UserResponse>> getMyInfo() {
        return ResponseEntity.ok(APIResponse.success(userService.getMyInfo()));
    }

    // 2. Tìm người dùng theo email (Thường dành cho Admin)
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(APIResponse.success(userService.getUserByEmail(email)));
    }

    // 3. Cập nhật trạng thái người dùng (Ví dụ: BAN, ACTIVE)
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<UserResponse>> updateStatus(
            @PathVariable String id,
            @RequestParam String status) {
        return ResponseEntity.ok(APIResponse.success(userService.updateStatus(id, status)));
    }

    // 4. Liệt kê người dùng theo trạng thái
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<APIResponse<List<UserResponse>>> getAllByStatus(@PathVariable String status) {
        return ResponseEntity.ok(APIResponse.success(userService.getAllUsersByStatus(status)));
    }
}
