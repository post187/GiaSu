package User.DTO.Request;

import Class.Entity.ClassStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ClassFilterRequest {
    ClassStatus status;
    String tutorId;
    String city;
    String subjectId;
    Boolean includeDeleted = false;
}