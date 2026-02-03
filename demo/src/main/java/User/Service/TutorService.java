package User.Service;

import User.DTO.Request.AvailabilityRequest;
import User.DTO.Request.TutorUpdateRequest;
import User.DTO.Request.UnavailabilityRequest;
import User.DTO.Request.VerificationSubmitRequest;
import User.DTO.Response.AvailabilityResponse;
import User.DTO.Response.TrustScoreResponse;
import User.DTO.Response.TutorProfilePublicResponse;
import User.DTO.Response.TutorProfileResponse;

import java.util.List;

public interface TutorService {
    public TutorProfileResponse getMyProfile();
    public TutorProfileResponse updateProfile(TutorUpdateRequest request);
    public TutorProfileResponse submitVerification(VerificationSubmitRequest request);

    public List<AvailabilityResponse> updateAvailability(List<AvailabilityRequest> request);

    public TutorProfilePublicResponse getPublicProfile(String tutorId);
    public TrustScoreResponse getTutorTrustScore(String tutorId)
    public List<ReviewResponse> getTutorReviews(String tutorId);
}
