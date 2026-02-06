package Class.Dto.Request;

import Class.Entity.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateClassRequest {
    private String subjectId;
    private String title;
    private String description;
    private String targetGrade;
    private Double pricePerHour;
    private LocationType locationType;
    private String city;
    private String district;
}
