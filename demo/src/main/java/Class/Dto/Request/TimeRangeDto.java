package Class.Dto.Request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TimeRangeDto {
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
