package Booking.Dto.Request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingCancelRequest {
    String reason;
}