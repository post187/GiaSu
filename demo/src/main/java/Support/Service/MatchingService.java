package Support.Service;

import Support.Dto.Request.MatchingRequest;
import Support.Dto.Request.TutorFilterRequest;
import Support.Dto.Response.TutorMatchResponse;

import java.util.List;

public interface MatchingService {
    // Legacy Filter (GET /tutors)
    List<TutorMatchResponse> searchTutors(TutorFilterRequest request);

    // Advanced Matching (POST /tutors)
    List<TutorMatchResponse> matchTutors(MatchingRequest request);
}