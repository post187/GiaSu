package Review.Service;

import Booking.Entity.Booking;
import Booking.Entity.BookingStatus;
import Booking.Repository.BookingRepository;
import Booking.Service.BookingService;
import Review.Dto.Request.ReviewRequest;
import Review.Dto.Response.ReviewResponse;
import Review.Entity.Review;
import Review.Repository.ReviewRepository;
import User.DTO.Response.UserResponse;
import User.Entity.StudentProfile;
import User.Repository.StudentProfileRepository;
import User.Repository.TutorProfileRepository;
import User.Service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingService bookingService;

    @Override
    public ReviewResponse createReview(ReviewRequest request) {
        UserResponse  userResponse = (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        StudentProfile student = studentProfileRepository.findByUserId(userResponse.getId())
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getStudentId().equals(student.getId())) {
            throw new RuntimeException("Booking not found"); // Giấu lỗi permission bằng 404 cho an toàn
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new RuntimeException("Booking not completed");
        }

        if (reviewRepository.existsByBookingId(booking.getId())) {
            throw new RuntimeException("Booking already has review");
        }
        Review review = Review.builder()
                .bookingId(booking.getId())
                .studentId(student.getId())
                .tutorId(booking.getTutorId())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        Review savedReview = reviewRepository.save(review);

        bookingService.recalculateTutorStats(booking.getTutorId());

        return mapToResponse(savedReview);

    }

    private ReviewResponse mapToResponse(Review entity) {
        return ReviewResponse.builder()
                .id(entity.getId())
                .bookingId(entity.getBookingId())
                .studentId(entity.getStudentId())
                .tutorId(entity.getTutorId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
