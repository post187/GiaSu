package User.Service;

import User.DTO.Response.UserResponse;
import User.Entity.Status;
import User.Entity.User;
import User.Mapper.UserMapper;
import User.Repository.UserRepository;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;


    @Override
    public UserResponse getMyInfo() {
        return (UserResponse) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not match"));

        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse updateStatus(String id, String status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("id not found"));

        user.setStatus(Status.valueOf(status));

        return userMapper.toUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsersByStatus(String status) {
        List<User> users = userRepository.findAllByStatus(status);

        return userMapper.toResponseList(users);
    }
}
