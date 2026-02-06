package Class.Dto.Response;

import Class.Entity.ClassLifecycleStatus;
import Class.Entity.ClassStatus;
import Class.Entity.LocationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassResponse {
    private String id;
    private String tutorId;
    private String subjectId;
    private String title;
    private String description;
    private String targetGrade;
    private Double pricePerHour;
    private LocationType locationType;
    private String city;
    private String district;
    private ClassStatus status;
    private ClassLifecycleStatus lifecycleStatus;
    private Integer totalSessions;
    private Integer sessionsCompleted;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
