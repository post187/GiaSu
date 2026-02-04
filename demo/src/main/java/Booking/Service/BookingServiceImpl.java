package Booking.Service;

import Booking.Dto.Request.BookingRequest;
import Booking.Dto.Response.BookingResponse;
import Booking.Entity.Booking;
import Booking.Entity.BookingStatus;
import Booking.Entity.CancelledBy;
import Booking.Repository.BookingRepository;
import Class.Repository.ClassRepository;
import Support.Entity.Schedule;
import Support.Entity.ScheduleStatus;
import Support.Repository.ScheduleRepository;
import User.Entity.StudentProfile;
import User.Entity.TutorProfile;
import User.Repository.StudentProfileRepository;
import User.Repository.TutorProfileRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingServiceImpl implements BookingService {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ClassRepository classRepository;

    @Autowired
    StudentProfileRepository studentRepository;

    @Autowired
    TutorProfileRepository tutorRepository;

    @Autowired
    ScheduleRepository scheduleRepository; // Bảng lưu lịch học cụ thể

    @Override
    @Transactional
    public BookingResponse createBooking(String userId, BookingRequest request) {
        // 1. Kiểm tra Student Profile
        StudentProfile student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        // 2. Kiểm tra lớp học
        Class.Entity.Class clazz = classRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("Class not found"));


        // 3. Tạo Booking mới
        Booking booking = Booking.builder()
                .classId(clazz.getId())
                .studentId(student.getId())
                .tutorId(clazz.getTutorId())
                .status(BookingStatus.PENDING)
                .isTrial(request.getIsTrial())
                .requestedHoursPerWeek(request.getRequestedHoursPerWeek())
                .noteFromStudent(request.getNoteFromStudent())
                .build();

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponse> getMyBookings(String userId, String role) {
        List<Booking> bookings;
        if ("TUTOR".equals(role)) {
            TutorProfile tutor = tutorRepository.findByUserId(userId);
            bookings = bookingRepository.findAllByTutorIdOrderByCreatedAtDesc(tutor.getId());
        } else {
            StudentProfile student = studentRepository.findByUserId(userId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));
            bookings = bookingRepository.findAllByStudentIdOrderByCreatedAtDesc(student.getId());
        }
        return bookings.stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional
    public void cancelBooking(String userId, String role, String bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Xác định ai là người hủy dựa trên Role
        if ("TUTOR".equals(role)) {
            TutorProfile tutor = tutorRepository.findByUserId(userId);
            if (!booking.getTutorId().equals(tutor.getId())) throw new RuntimeException("Forbidden");
            booking.setCancelledBy(CancelledBy.TUTOR);
        } else if ("STUDENT".equals(role)) {
            StudentProfile student = studentRepository.findByUserId(userId).get();
            if (!booking.getStudentId().equals(student.getId())) throw new RuntimeException("Forbidden");
            booking.setCancelledBy(CancelledBy.STUDENT);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelReason(reason);
        bookingRepository.save(booking);

        // Xóa các lịch học tương lai khi bị hủy
        scheduleRepository.deleteByBookingId(bookingId);

        // Cập nhật lại uy tín gia sư
        recalculateTutorStats(booking.getTutorId());
    }

    @Override
    @Transactional
    public void confirmBooking(String userId, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Kiểm tra quyền (chỉ gia sư của lớp này mới được confirm)
        TutorProfile tutor = tutorRepository.findByUserId(userId);
        if (!booking.getTutorId().equals(tutor.getId())) throw new RuntimeException("Forbidden");

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Logic tạo Schedule tự động cho 4 tuần tới (giống file .ts)
        generateInitialSchedules(booking);
    }

    @Override
    @Transactional
    public void completeBooking(String userId, String role, String bookingId) {
        Booking booking = bookingRepository.findById(bookingId).get();

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // Xóa các lịch học dự kiến còn lại
        scheduleRepository.deleteByBookingId(bookingId);

        // Gọi hàm tính lại chỉ số uy tín (Trust Score) cho gia sư
        recalculateTutorStats(booking.getTutorId());
    }

    private void generateInitialSchedules(Booking booking) {
        // Logic giả định: Lấy ngày bắt đầu và cộng thêm 4 tuần
        LocalDateTime startDate = booking.getStartDateExpected();
        for (int i = 0; i < 4; i++) {
            Schedule session = new Schedule();
            session.setBookingId(booking.getId());
            session.setTutorId(booking.getTutorId());
            session.setStudentId(booking.getStudentId());
            session.setStartTime(startDate.plusWeeks(i)); // Tạm thời lấy giờ từ startDate
            session.setEndTime(startDate.plusWeeks(i).plusHours(2)); // Mặc định 2 tiếng
            session.setStatus(ScheduleStatus.ACTIVE);
            scheduleRepository.save(session);
        }
    }

    // Hàm hỗ trợ tìm ngày theo DayOfWeek (giống getDateByDayOfWeek trong file .ts)
    private LocalDateTime getDateForWeek(LocalDateTime base, int targetDayOfWeek) {
        int currentDay = base.getDayOfWeek().getValue() % 7; // Chuyển về 0 (CN) -> 6 (T7)
        int diff = (targetDayOfWeek + 7 - currentDay) % 7;
        return base.plusDays(diff);
    }

    @Transactional
    public void recalculateTutorStats(String tutorId) {
        TutorProfile tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor profile not found"));

        // 1. Đếm tổng số booking theo từng trạng thái
        long completedCount = bookingRepository.countByTutorIdAndStatus(tutorId, BookingStatus.COMPLETED);
        long cancelledCount = bookingRepository.countByTutorIdAndStatus(tutorId, BookingStatus.CANCELLED);
        long totalBookings = bookingRepository.countByTutorId(tutorId);

        // 2. Tính toán Trust Score (Điểm uy tín)
        // Công thức ví dụ: Điểm gốc 100, mỗi lần hoàn thành +2, mỗi lần bị hủy bởi gia sư -10
        double baseScore = 50.0; // Điểm khởi đầu
        double finalScore = baseScore + (completedCount * 2) - (cancelledCount * 5);

        // Giới hạn trong khoảng 0 - 100
        finalScore = Math.max(0, Math.min(100, finalScore));

        // 3. Cập nhật vào Entity
        tutor.setTotalCompletedBookings((int) completedCount);
        tutor.setTotalCancelledBookings((int) cancelledCount);
        tutor.setTotalBookings((int) totalBookings);
        tutor.setTrustScore(finalScore);
        tutor.setLastTrustScoreUpdatedAt(LocalDateTime.now());

        tutorRepository.save(tutor);
    }

    private BookingResponse mapToResponse(Booking b) {
        return BookingResponse.builder()
                .id(b.getId())
                .status(b.getStatus())
                .isTrial(b.getIsTrial())
                .createdAt(b.getCreatedAt())
                .build();
    }
}
