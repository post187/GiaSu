package Class.Dto.Response;

import Class.Entity.SessionStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SessionResponse {
    private String id;
    private String startAt;
    private String endAt;
    private String status;
}
