package Auth.Controller;

import Auth.Dto.Request.LoginRequest;
import Auth.Dto.Request.LogoutRequest;
import Auth.Dto.Request.RegisterRequest;
import Auth.Dto.Response.LoginResponse;
import Auth.Service.AuthService;
import Config.APIResponse;
import Config.Jwt.JwtUtil;
import User.Repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<APIResponse<LoginResponse>> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authService.register(request);

        setRefreshTokenCookie(response, loginResponse.getRefreshToken());

        loginResponse.setRefreshToken(null);

        return ResponseEntity.ok(APIResponse.success(loginResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponse>> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse loginResponse = authService.login(request);

        setRefreshTokenCookie(response, loginResponse.getRefreshToken());

        loginResponse.setRefreshToken(null);

        return ResponseEntity.ok(APIResponse.success(loginResponse));
    }

    @PostMapping("log-out")
    public ResponseEntity<APIResponse<?>> logout (
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(LogoutRequest.builder()
                .token(refreshToken)
                .build());

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true) // true nếu HTTPS
                .path("/api/auth/refresh")
                .maxAge(0) // xoá cookie
                .sameSite("None") // giống lúc set
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return ResponseEntity.ok(APIResponse.success("Log-out success"));
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // để true khi deploy HTTPS
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 ngày
                .sameSite("Strict")
                .build();
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
