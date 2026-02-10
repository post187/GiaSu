package Review.Repository;

import Review.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, String> {
    boolean existsByBookingId(String bookingId);
}
