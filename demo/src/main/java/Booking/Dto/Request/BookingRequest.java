package Booking.Dto.Request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequest {
    String classId;
    Boolean isTrial;
    Integer requestedHoursPerWeek;
    LocalDateTime startDateExpected;
    String noteFromStudent;

    PreferredSlotDTO preferredSlot;

    @Data
    public static class PreferredSlotDTO {
        int dayOfWeek; // 0 (CN) -> 6 (T7)
        String startTime; // "18:00"
        String endTime; // "20:00"
    }
}