package Config.Entity;

import User.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void revokeAllByUser(User user);
    void deleteByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
