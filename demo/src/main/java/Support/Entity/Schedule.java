package Support.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String tutorId;
    private String studentId;
    private String bookingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Enumerated(EnumType.STRING)
    private ScheduleStatus status = ScheduleStatus.ACTIVE;
}