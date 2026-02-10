package Booking.Service;

import Booking.Dto.Request.BookingRequest;
import Booking.Dto.Response.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(String userId, BookingRequest request);
    List<BookingResponse> getMyBookings(String userId, String role);
    void cancelBooking(String userId, String role, String bookingId, String reason);
    void confirmBooking(String userId, String bookingId);
    void completeBooking(String userId, String role, String bookingId);

    void recalculateTutorStats(String tutorId);
}
