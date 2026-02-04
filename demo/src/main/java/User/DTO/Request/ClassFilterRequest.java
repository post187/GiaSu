package User.DTO.Request;

import Class.Entity.ClassStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassFilterRequest {
    ClassStatus status;
    String tutorId;
    String city;
    String subjectId;
    Boolean includeDeleted = false;
}