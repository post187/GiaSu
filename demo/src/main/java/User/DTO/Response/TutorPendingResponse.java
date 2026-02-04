package User.DTO.Response;

import User.Entity.VerificationStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TutorPendingResponse {
    String id;
    String fullName;
    String email;
    String nationalIdNumber; // Sẽ được mask (ẩn số)
    String nationalIdFrontImageUrl;
    String nationalIdBackImageUrl;
    LocalDateTime verificationSubmittedAt;
    VerificationStatus verificationStatus;
}