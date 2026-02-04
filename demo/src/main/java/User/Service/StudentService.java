package User.Service;

import User.DTO.Request.StudentUpdateRequest;
import User.DTO.Response.StudentProfileResponse;

public interface StudentService {
    StudentProfileResponse getMyProfile();
    StudentProfileResponse updateProfile(StudentUpdateRequest dto);
}