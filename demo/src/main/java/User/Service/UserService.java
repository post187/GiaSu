package User.Service;

import User.DTO.Response.UserResponse;

import java.util.List;

public interface UserService {
    public UserResponse getMyInfo();
    public UserResponse getUserByEmail(String email);
    UserResponse updateStatus(String id, String status);
    List<UserResponse> getAllUsersByStatus(String status);
}
