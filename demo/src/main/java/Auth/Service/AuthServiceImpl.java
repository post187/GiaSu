package Auth.Service;

import Auth.Dto.Request.LoginRequest;
import Auth.Dto.Request.LogoutRequest;
import Auth.Dto.Request.RegisterRequest;
import Auth.Dto.Response.LoginResponse;
import Config.Entity.RefreshToken;
import Config.Entity.RefreshTokenRepository;
import Config.Jwt.JwtUtil;
import User.Entity.Role;
import User.Entity.StudentProfile;
import User.Entity.TutorProfile;
import User.Entity.User;
import User.Repository.StudentProfileRepository;
import User.Repository.TutorProfileRepository;
import User.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not correct"));

        if (!passwordEncoder.matches(user.getPasswordHash(), request.getPassword())) {
            throw new RuntimeException("Password not match");
        }

        // generate Token
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        refreshTokenRepository.revokeAllByUser(user);

        RefreshToken saverToken = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(saverToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    @Override
    public LoginResponse register(RegisterRequest request) {
        boolean exist = userRepository.existByEmailOrPhone(request.getEmail(), request.getPhone());

        if (exist) {
            throw new RuntimeException("Email or phone already used");
        }

        String passwordHash = passwordEncoder.encode(request.getPassword());

        User newUser = User.builder()
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordHash)
                .fullName(request.getFullName())
                .role(Role.valueOf(request.getRole()))
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(newUser);
        if (newUser.getRole() == Role.STUDENT) {
            StudentProfile studentProfile = StudentProfile.builder()
                    .user(newUser)
                    .gradeLevel("UnKnow")
                    .preferredSubject(new ArrayList<>())
                    .build();

            studentProfileRepository.save(studentProfile);
        }

        if (newUser.getRole() == Role.TEACHER) {
            TutorProfile tutorProfile = TutorProfile.builder()
                    .user(newUser)
                    .build();

            tutorProfileRepository.save(tutorProfile);
        }

        String accessToken = jwtUtil.generateAccessToken(newUser);
        String refreshToken = jwtUtil.generateRefreshToken(newUser);

        RefreshToken saverToken = RefreshToken.builder()
                .user(newUser)
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        refreshTokenRepository.save(saverToken);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();

    }

    @Override
    public void logout(LogoutRequest request) {
        String refreshToken = request.getToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        String email = jwtUtil.getEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken tokenEntity = refreshTokenRepository.findByUser(user)
                .orElse(null);

        if (tokenEntity != null) {
            tokenEntity.setRevoked(true);
            refreshTokenRepository.save(tokenEntity);
        }
    }
}
