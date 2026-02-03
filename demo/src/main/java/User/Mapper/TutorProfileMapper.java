package User.Mapper;

import User.DTO.Response.TutorProfilePublicResponse;
import User.DTO.Response.TutorProfileResponse;
import User.Entity.TutorProfile;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TutorProfileMapper {
    TutorProfileResponse toResponse(TutorProfile profile);
    TutorProfilePublicResponse toPublicResponse(TutorProfile profile);
}
