package User.Repository;

import User.DTO.Response.TrustScoreResponse;
import User.Entity.TutorProfile;
import User.Entity.User;
import User.Entity.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, String> {
    TutorProfile findByUserId(String userId);

    @Query("SELECT new com.example.dto.TrustScoreResponse(t.averageRating, t.trustScore, t.totalReviews, t.totalCompletedBookings) " +
            "FROM TutorProfile t WHERE t.id = :tutorId")
    Optional<TrustScoreResponse> findTrustScoreByTutorId(@Param("tutorId") String tutorId);

    @Query("SELECT t FROM TutorProfile t JOIN t.user u " +
            "WHERE t.verificationStatus = 'PENDING' " +
            "AND u.status IN ('PENDING', 'ACTIVE') " +
            "ORDER BY t.createdAt ASC")
    List<TutorProfile> findPendingTutors();

    @Query("SELECT DISTINCT t FROM TutorProfile t " +
            "JOIN FETCH t.user u " +
            "LEFT JOIN FETCH t.classes c " + // Eager fetch classes để tính toán
            "WHERE t.verificationStatus = :status " +
            "AND u.status = 'ACTIVE' " +
            "AND (:city IS NULL OR t.city = :city) " +
            "AND (:district IS NULL OR t.district = :district)")
    List<TutorProfile> findPotentialTutors(
            @Param("status") VerificationStatus status,
            @Param("city") String city,
            @Param("district") String district
    );


}
