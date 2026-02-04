package User.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentUpdateRequest {
    private String gradeLevel;
    private String goals;
    private List<String> preferredSubjects;
    private String notes;
}
