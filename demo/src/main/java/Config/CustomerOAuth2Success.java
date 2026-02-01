package Config;

import Config.Jwt.JwtUtil;
import User.Entity.Role;
import User.Entity.Status;
import User.Entity.User;
import User.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.KeyLengthException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

public class CustomerOAuth2Success implements AuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;

        OAuth2User user = oAuth2AuthenticationToken.getPrincipal();
        String email = user.getAttribute("email");

        String firstName;
        String lastName;

        String registrationId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();

        switch (registrationId.toLowerCase()) {
            case "google":
                firstName = user.getAttribute("family_name");
                lastName = user.getAttribute("given_name");
                break;
            case "facebook":
                firstName = user.getAttribute("first_name");
                lastName = user.getAttribute("last_name");
                break;
            default:
                firstName = "";
                lastName = "";
        }
        User user1 = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setPasswordHash(""); // OAuth user không cần password
                    newUser.setFullName(firstName + " " + lastName);
                    newUser.setPhone("00000000000000");
                    newUser.setRole(Role.STUDENT);
                    newUser.setStatus(Status.ACTIVE);
                    newUser.setCreatedAt(LocalDateTime.now());
                    return userRepository.save(newUser);
                });
        String accessToken;
        String refreshToken;
        accessToken = jwtUtil.generateAccessToken(user1);
        refreshToken = jwtUtil.generateRefreshToken(user1);

        // Đặt refresh token vào cookie
        setRefreshTokenCookie(response, refreshToken);

        // Trả về JSON response
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        APIResponse<LoginResponse> responseWrapper = APIResponse.success(loginResponse);
        response.getWriter().write(objectMapper.writeValueAsString(responseWrapper));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // true khi deploy HTTPS
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 ngày
                .sameSite("Lax") // Lax để OAuth redirect không bị chặn
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    }




}
