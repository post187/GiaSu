package User.Repository;

import User.Entity.TutorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TutorAvailabilityRepository extends JpaRepository<TutorAvailability, String> {
    // Xóa toàn bộ lịch rảnh của một gia sư
    void deleteByTutorId(String tutorId);

    // Tìm kiếm để trả về sau khi cập nhật
    List<TutorAvailability> findByTutorIdOrderByDayOfWeekAscStartMinuteAsc(String tutorId);
}