package User.Repository;

import User.Entity.StudentProfile;
import User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, String> {
    Optional<StudentProfile> findByUserId(String userId);
    Optional<StudentProfile> findByUser(User user);
}