package Auth.Service;


import Auth.Dto.Request.LoginRequest;
import Auth.Dto.Request.LogoutRequest;
import Auth.Dto.Request.RegisterRequest;
import Auth.Dto.Response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse register(RegisterRequest request);

    void logout(LogoutRequest request);
}
