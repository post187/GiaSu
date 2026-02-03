package User.DTO.Request;

import User.Entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @NotEmpty(message = "Fullname is requirement")
    private String fullName;

    @Email(message = "Email not correct")
    @NotEmpty(message = "Email is requirement")
    private String email;

    @Size(min = 8, message = "Password more 8 character")
    private String password;

    private Role role;
}
