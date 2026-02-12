package Support.Dto.Response;

import User.DTO.Response.TutorProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TutorMatchResponse {
    private TutorProfileResponse tutor;
    private double matchScore;
}
