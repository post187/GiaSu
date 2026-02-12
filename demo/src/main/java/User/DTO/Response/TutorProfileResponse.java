package User.DTO.Response;

import User.Entity.VerificationStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TutorProfileResponse {
    private String id;
    private String userId;
    private String bio;
    private String education;
    private List<String> certificates;
    private Integer yearsOfExperience;
    private Double hourlyRateMin;
    private Double hourlyRateMax;
    private List<String> teachingModes;
    private String city;
    private String district;

    // Thông tin định danh (đã mask như trong tutorSanitizer.ts)
    private String nationalIdNumber;
    private String nationalIdFrontImageUrl;
    private String nationalIdBackImageUrl;

    private VerificationStatus verificationStatus;
    private Double averageRating;
    private Double trustScore;
}