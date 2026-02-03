package User.DTO.Request;

import Class.Entity.LocationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AvailabilityRequest {
    @Min(0) @Max(6)
    private int dayOfWeek;

    @Min(0) @Max(1440)
    private int startMinute;

    @Min(1) @Max(1440)
    private int endMinute;

    @NotBlank
    private String timezone = "UTC";

    // Enum LocationType nên được định nghĩa trong Java
    private LocationType locationType;
}

// Lưu ý: Khi nhận Request, bạn sẽ nhận List<AvailabilityDTO>
