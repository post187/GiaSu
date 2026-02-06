package Class.Repository;

import Class.Entity.ClassSchedule;
import Support.Entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, String> {
    void deleteByClassId(String classId);

    Optional<ClassSchedule> findByClassId(String classId);
}
