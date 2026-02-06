package Class.Repository;

import Class.Entity.ClassStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    List<Class.Entity.Class> findByTutorIdAndIsDeletedFalse(String tutorId);

    // Tìm lớp công khai
    Optional<Class.Entity.Class> findByIdAndIsDeletedFalse(String id);

    @Query("SELECT c FROM ClassEntity c WHERE " +
            "(:tutorId IS NULL OR c.tutorId = :tutorId) AND " +
            "(:subjectId IS NULL OR c.subjectId = :subjectId) AND " +
            "(:status IS NULL OR c.status = :status) AND " +
            "(:city IS NULL OR c.city = :city) AND " +
            "(:district IS NULL OR c.district = :district) AND " +
            "(:includeDeleted = TRUE OR c.isDeleted = FALSE)")
    Page<Class.Entity.Class> searchClasses(
            @Param("tutorId") String tutorId,
            @Param("subjectId") String subjectId,
            @Param("status") ClassStatus status,
            @Param("city") String city,
            @Param("district") String district,
            @Param("includeDeleted") Boolean includeDeleted,
            Pageable pageable
    );

    @Query("SELECT c FROM ClassEntity c WHERE c.id = :id AND c.isDeleted = false")
    Optional<Class.Entity.Class> findActiveById(@Param("id") String id);

    List<Class.Entity.Class> findAllByTutorIdAndIsDeletedFalseOrderByCreatedAtDesc(String tutorId);
}
