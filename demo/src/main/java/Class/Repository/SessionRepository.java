package Class.Repository;

import Class.Entity.Session;
import Class.Entity.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
    // Tìm tất cả buổi học của một lớp
    List<Session> findAllByClassIdOrderByScheduledStartAtAsc(String classId);

    // Tìm lịch học trong khoảng thời gian (cho Calendar)
    @Query("SELECT s FROM Session s WHERE s.classId = :classId " +
            "AND s.scheduledStartAt BETWEEN :start AND :end")
    List<Session> findSessionsInPeriod(String classId, LocalDateTime start, LocalDateTime end);

    // Tìm các buổi cần điểm danh của User cụ thể (thông qua Class -> Tutor/Student)
    @Query("SELECT s FROM Session s JOIN s.clazz c " +
            "WHERE (c.tutorId = :profileId OR s.id IN (SELECT b.id FROM Booking b WHERE b.studentId = :profileId)) " +
            "AND s.status = 'SCHEDULED'")
    List<Session> findUpcomingSessions(String profileId);

    @Modifying
    @Query("DELETE FROM Session s WHERE s.classId = :classId AND s.status != 'COMPLETED'")
    void deleteNonCompletedSessions(String classId);

    long countByClassIdAndStatus(String classId, SessionStatus status);
    void deleteByClassId(String classId);

    @Query("SELECT s FROM Session s " +
            "JOIN s.clazz c " +
            "WHERE c.tutorId = :tutorId " +
            "AND s.classId != :excludedClassId " +
            "AND s.status != 'CANCELLED' " +
            "AND s.scheduledEndAt > :now")
    List<Session> findActiveSessionsByTutor(@Param("tutorId") String tutorId,
                                            @Param("excludedClassId") String excludedClassId,
                                            @Param("now") LocalDateTime now);

    List<Session> findByClassIdOrderByScheduledStartAtAsc(String classId);

    long countByClassId(String classId);

    // Xóa các session chưa diễn ra (SCHEDULED)
    @Modifying
    @Query("DELETE FROM Session s WHERE s.classId = :classId AND s.status = 'SCHEDULED'")
    void deleteFutureSessions(String classId);
}
