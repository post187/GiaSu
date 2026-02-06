package Class.Dto.Request;


import Class.Entity.ClassStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassFilterRequest {
    private String tutorId;
    private String subjectId;
    private ClassStatus status;
    private String city;
    private String district;
    private Boolean includeDeleted = false;

    // Pagination
    private int page = 1;
    private int pageSize = 20;
}
