package User.Service;

import User.DTO.Request.ClassFilterRequest;
import User.DTO.Response.ClassAdminResponse;
import User.DTO.Response.TutorPendingResponse;

import java.util.List;

public interface AdminService {
    // Quản lý Gia sư
    List<TutorPendingResponse> getPendingTutors();
    void approveTutor(String tutorId);
    void rejectTutor(String tutorId, String note);

    // Quản lý Lớp học
    List<ClassAdminResponse> getClasses(ClassFilterRequest filter);
}
