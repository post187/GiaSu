package Class.Service;


import Booking.Dto.Response.BookingResponse;
import Class.Dto.Request.ClassRequest;
import Class.Dto.Request.ClassFilterRequest;
import Class.Dto.Request.ScheduleRequest;
import Class.Dto.Request.UpdateClassRequest;
import Class.Dto.Response.ClassDetailResponse;
import Class.Dto.Response.ClassResponse;
import Class.Dto.Response.ClassScheduleResponse;
import Class.Entity.ClassStatus;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ClassService {
    Page<ClassResponse> getClasses(ClassFilterRequest filter);

    // 2. Lấy chi tiết lớp học (Public)
    ClassResponse getClassById(String id);

    // 3. Tạo lớp học mới (Tutor)
    ClassResponse createClass(String userId, ClassRequest request);

    // 4. Cập nhật thông tin lớp học (Tutor)
    ClassResponse updateClass(String id, String userId, UpdateClassRequest request);

    // 5. Cập nhật trạng thái lớp (Publish/Draft/Archive)
    ClassResponse updateClassStatus(String id, String userId, ClassStatus status);

    // 6. Hủy lớp học (Tutor/Admin) - Có logic hoàn tiền
    void cancelClass(String id, String userId, String reason);

    // 7. Xóa mềm lớp học (Soft Delete)
    void deleteClass(String id, String userId);

    // 8. Lấy danh sách học viên đã đăng ký (Tutor/Admin)
    List<BookingResponse> getClassStudents(String id, String userId);

    // 9. Lấy danh sách buổi học (Sessions)
    ClassDetailResponse getClassSessions(String id, String userId, String userRole);

    // 10. Lấy cấu hình lịch học (Schedule)
    ClassScheduleResponse getClassSchedule(String id);

    // 11. Xóa lịch học và các buổi học trong tương lai
    void clearSchedule(String id, String userId);

    // 12. Tạo/Cập nhật lịch học (Logic phức tạp nhất: Recurrence -> Sessions)
    ClassScheduleResponse updateSchedule(String id, String userId, ScheduleRequest request);
}

