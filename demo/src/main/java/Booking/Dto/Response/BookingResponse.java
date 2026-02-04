package Booking.Dto.Response;

import Booking.Entity.BookingStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {
    String id;
    String classId;
    String className;
    String studentName;
    String tutorName;
    BookingStatus status;
    Boolean isTrial;
    LocalDateTime createdAt;
    // Thêm các trường khác cần thiết cho UI
}
