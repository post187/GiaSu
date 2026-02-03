package User.Service;

import User.DTO.Request.TutorUpdateRequest;
import User.DTO.Request.VerificationSubmitRequest;
import User.DTO.Response.TutorProfileResponse;

public interface TutorService {
    public TutorProfileResponse getMyProfile();
    public TutorProfileResponse updateProfile(TutorUpdateRequest request);
    public TutorProfileResponse submitVerification(VerificationSubmitRequest request);

}
