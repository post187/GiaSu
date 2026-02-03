package User.Mapper;

import Auth.Dto.Request.RegisterRequest;
import User.DTO.Response.UserResponse;
import User.Entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequest request);
    UserResponse toUserResponse(User user);

    User toUser(UserResponse userResponse);

    @IterableMapping(elementTargetType = UserResponse.class)
    List<UserResponse> toResponseList(List<User> users);


}

