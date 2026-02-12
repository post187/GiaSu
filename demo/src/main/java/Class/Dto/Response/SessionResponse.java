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
    private String classId;
    private LocalDateTime scheduledStartAt;
    private LocalDateTime scheduledEndAt;
    private SessionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime disputeFlaggedAt;

    // Confirmation status
    private boolean tutorStartConfirmed;
    private boolean studentStartConfirmed;
    private boolean tutorCompleteConfirmed;
    private boolean studentCompleteConfirmed;
}
