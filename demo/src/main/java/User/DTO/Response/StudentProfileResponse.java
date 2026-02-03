package User.DTO.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StudentProfileResponse {
    private String id;
    private String userId;
    private String gradeLevel;
    private List<String> preferredSubjects;
    private String goals;
}