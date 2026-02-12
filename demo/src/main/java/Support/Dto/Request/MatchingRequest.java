package Support.Dto.Request;


import Class.Dto.Request.SlotDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class MatchingRequest {
    @NotBlank(message = "Subject ID is required")
    private String subjectId;

    @NotBlank(message = "Grade level is required")
    private String gradeLevel;

    private String city;
    private String district;

    @NotNull
    @Positive(message = "Budget must be positive")
    private Double budgetPerHour;

    @Valid
    private List<SlotDto> desiredSlots;

    private String description;

    @Min(1)
    private int limit = 10;
}
