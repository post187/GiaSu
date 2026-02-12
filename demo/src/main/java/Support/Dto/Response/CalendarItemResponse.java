package Support.Dto.Response;

import Booking.Entity.BookingStatus;
import Class.Entity.LocationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarItemResponse {
    private String id; // Booking ID

    // Thông tin hiển thị tùy ngữ cảnh (Tutor xem Student, Student xem Tutor)
    private String studentName;
    private String tutorName;

    private String subjectName;
    private String className;

    // Thời gian học (Parse từ JSON note)
    private Integer dayOfWeek; // 0-6
    private String startTime;  // "HH:mm"
    private String endTime;    // "HH:mm"

    private BookingStatus status;
    private LocationType locationType;
}