package User.Repository;

import User.Entity.TutorProfile;
import User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, String> {
    TutorProfile findByUser(User user);
}
