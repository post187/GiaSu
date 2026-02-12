package Support.Dto.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TutorFilterRequest {
    private String studentId;
    private String subjectId;
    private String city;
    private String district;
    private Double priceMin;
    private Double priceMax;
    private String gradeLevel;
}
