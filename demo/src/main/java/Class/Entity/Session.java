package Class.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Session {

    @Id @GeneratedValue(strategy = GenerationType.UUID)
     String id;
     String classId;
    @ManyToOne @JoinColumn(name = "classId", insertable = false, updatable = false)
     Class clazz;

     LocalDateTime scheduledStartAt;
     LocalDateTime scheduledEndAt;
    @Enumerated(EnumType.STRING)  SessionStatus status = SessionStatus.SCHEDULED;

     LocalDateTime tutorStartConfirmedAt;
     LocalDateTime studentStartConfirmedAt;
     LocalDateTime startedAt;
     LocalDateTime completedAt;
}

