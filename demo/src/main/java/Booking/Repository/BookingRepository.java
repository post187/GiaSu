package Booking.Repository;

import Booking.Entity.Booking;
import Booking.Entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
