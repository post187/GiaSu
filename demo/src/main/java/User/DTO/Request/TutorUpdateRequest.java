package User.DTO.Request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TutorUpdateRequest {
    private String bio;
    private String education;
    private List<String> certificates;

    @Min(0)
    private Integer yearsOfExperience;

    @DecimalMin("0.0")
    private Double hourlyRateMin;

    @DecimalMin("0.0")
    private Double hourlyRateMax;

    private List<String> teachingModes;
    private String city;
    private String district;
}
