package Class.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ClassScheduleResponse {
    private String scheduleId;
    private String timezone;
    private Object recurrenceRule; // Trả về Object để FE dễ đọc thay vì chuỗi JSON
    private Object explicitSessions;
}