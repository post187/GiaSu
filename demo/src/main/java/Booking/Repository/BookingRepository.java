package Booking.Repository;

import Booking.Entity.Booking;
import Booking.Entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    // Lấy danh sách cho học sinh
    List<Booking> findAllByStudentIdOrderByCreatedAtDesc(String studentId);

    // Lấy danh sách cho gia sư
    List<Booking> findAllByTutorIdOrderByCreatedAtDesc(String tutorId);

    long countByTutorIdAndStatus(String tutorId, BookingStatus status);
    long countByTutorId(String tutorId);

    // Đếm active bookings (Cho logic không cho xóa lớp nếu còn booking)
    // Active thường là: PENDING, CONFIRMED, TRIAL (Tùy logic của bạn)
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.classInfo.id = :classId " +
            "AND b.status IN ('PENDING', 'CONFIRMED', 'TRIAL')")
    long countActiveBookings(@Param("classId") String classId);

    // Lấy danh sách booking active (Để hủy lớp thì hủy luôn booking)
    @Query("SELECT b FROM Booking b WHERE b.classInfo.id = :classId " +
            "AND b.status IN ('PENDING', 'CONFIRMED', 'TRIAL')")
    List<Booking> findActiveBookingsByClassId(@Param("classId") String classId);

    // Check xem user (student) có booking nào đã confirm trong lớp này chưa
    // Cần join bảng StudentProfile nếu Booking lưu studentId
    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Booking b " +
            "JOIN b.student s " +
            "WHERE b.classInfo.id = :classId " +
            "AND s.userId = :userId " + // Giả sử StudentProfile có trường userId mapping với User account
            "AND b.status = :status")
    boolean existsByClassIdAndUserIdAndStatus(
            @Param("classId") String classId,
            @Param("userId") String userId,
            @Param("status") BookingStatus status);
}
