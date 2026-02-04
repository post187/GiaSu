package User.DTO.Request;


import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TutorRejectRequest {
    @NotBlank(message = "Lý do từ chối không được để trống")
    String note;
}
