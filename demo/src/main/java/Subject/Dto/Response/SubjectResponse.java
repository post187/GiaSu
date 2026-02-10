package Subject.Dto.Response;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SubjectResponse {
    private String id;
    private String name;
    private String level;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
