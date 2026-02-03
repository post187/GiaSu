package User.DTO.Response;

import User.Entity.Role;
import User.Entity.Status;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private String id;
    private String fullName;
    private String email;
    private Role role;
    private Status status;
    private LocalDateTime createdAt;
}