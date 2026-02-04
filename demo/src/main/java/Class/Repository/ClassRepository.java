package Class.Repository;

import Class.Entity.ClassStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class.Entity.Class, String> {
    @Query("SELECT c FROM ClassEntity c WHERE " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:tutorId IS NULL OR c.tutorId = :tutorId) AND " +
            "(:city IS NULL OR c.city = :city) AND " +
            "(:subjectId IS NULL OR c.subjectId = :subjectId) AND " +
            "(:includeDeleted = true OR c.isDeleted = false) " +
            "ORDER BY c.createdAt DESC")
    List<Class.Entity.Class> findByAdminFilter(
            @Param("status") ClassStatus status,
            @Param("tutorId") String tutorId,
            @Param("city") String city,
            @Param("subjectId") String subjectId,
            @Param("includeDeleted") boolean includeDeleted
    );
}
