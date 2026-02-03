package User.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UnavailabilityRequest {
    @NotNull
    private LocalDateTime startAt;

    @NotNull
    private LocalDateTime endAt;
}
