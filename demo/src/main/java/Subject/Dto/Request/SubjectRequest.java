package Subject.Dto.Request;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SubjectRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Level is required")
    private String level;

    private String description;
}