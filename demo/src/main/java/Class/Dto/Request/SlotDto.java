package Class.Dto.Request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class SlotDto {
    @Min(0) @Max(6) private Integer dayOfWeek;
    @Min(0) @Max(1440) private Integer startMinute;
    @Min(1) @Max(1440) private Integer endMinute;
}
