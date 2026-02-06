package Class.Dto.Request;

import Class.Entity.LocationType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassRequest {
    @NotBlank(message = "Subject ID is required")
    private String subjectId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String targetGrade;

    @NotNull
    @Min(value = 0, message = "Price must be positive")
    private Double pricePerHour;

    @NotNull(message = "Location type is required")
    private LocationType locationType;

    private String city;
    private String district;
}