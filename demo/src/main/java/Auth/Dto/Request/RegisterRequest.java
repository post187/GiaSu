package Auth.Dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequest {
    @NotEmpty(message = "Email is requirement")
    @Email(message = "Invalid")
    String email;

    @NotEmpty(message = "Password is requirement")
    @Size(min = 8, message = "Password length more 8 character")
    String password;

    @NotEmpty(message = "FullName is requirement")
    String fullName;

    @NotEmpty(message = "Phone number is requirement")
    String phone;

    @NotEmpty(message = "Role is requirement")
    String role;
}
