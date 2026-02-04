package Booking.Controller;

import Booking.Dto.Request.BookingCancelRequest;
import Booking.Dto.Request.BookingRequest;
import Booking.Dto.Response.BookingResponse;
import Booking.Service.BookingService;
import User.DTO.Response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Học sinh đặt lớp
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<BookingResponse> create(
            @RequestBody BookingRequest request) {
        UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(bookingService.createBooking(userResponse.getId(), request));
    }

    // Xem danh sách đặt lớp của tôi (Gia sư hoặc Học sinh)
    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyRole('STUDENT', 'TUTOR')")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        UserResponse userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(bookingService.getMyBookings(userResponse.getId(), String.valueOf(userResponse.getRole())));
    }

    // Gia sư xác nhận dạy
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<Void> confirm(
            @PathVariable String id) {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookingService.confirmBooking(user.getId(), id);
        return ResponseEntity.ok().build();
    }

    // Hủy đặt lớp
    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('STUDENT', 'TUTOR', 'ADMIN')")
    public ResponseEntity<Void> cancel(
            @PathVariable String id,
            @RequestBody BookingCancelRequest request) {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        bookingService.cancelBooking(user.getId(), String.valueOf(user.getRole()), id, request.getReason());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<Void> complete(
            @PathVariable String id) {
        UserResponse user = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookingService.completeBooking(user.getId(), String.valueOf(user.getRole()), id);
        return ResponseEntity.ok().build();
    }


}
