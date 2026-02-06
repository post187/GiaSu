package Class.Dto.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RecurrenceDto {
    @NotBlank
    private String startDate; // yyyy-MM-dd
    @Min(1) @Max(52) private Integer weeks;
    @NotEmpty
    private List<SlotDto> slots;
}
