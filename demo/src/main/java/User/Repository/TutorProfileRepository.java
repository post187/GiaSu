package User.Repository;

import User.DTO.Response.TrustScoreResponse;
import User.Entity.TutorProfile;
import User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, String> {
    TutorProfile findByUser(User user);

    @Query("SELECT new com.example.dto.TrustScoreResponse(t.averageRating, t.trustScore, t.totalReviews, t.totalCompletedBookings) " +
            "FROM TutorProfile t WHERE t.id = :tutorId")
    Optional<TrustScoreResponse> findTrustScoreByTutorId(@Param("tutorId") String tutorId);
}
