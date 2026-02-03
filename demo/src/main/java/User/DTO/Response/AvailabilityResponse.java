package User.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityResponse {
    private Integer dayOfWeek;   // 0-6
    private Integer startMinute; // Ví dụ: 480 (8:00 AM)
    private Integer endMinute;   // Ví dụ: 600 (10:00 AM)
    private String timezone;
    private String locationType; // ONLINE, HOME, TUTOR_HOME
}