package User.DTO.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TutorProfilePublicResponse {
    private String id;
    private String userId;
    private String fullName;
    private String bio;
    private String education;
    private List<String> certificates;
    private Integer yearsOfExperience;
    private Double hourlyRateMin;
    private Double hourlyRateMax;
    private List<String> teachingModes;
    private String city;
    private String district;
    private String verificationStatus; // PENDING, VERIFIED, etc.
    private Double averageRating;
    private Integer trustScore;
}