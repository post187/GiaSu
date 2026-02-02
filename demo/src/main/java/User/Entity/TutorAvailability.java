package User.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TutorAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "tutor_id")
    private TutorProfile tutor;

    private Integer dayOfWeek; // 0-6
    private Integer startMinute;
    private Integer endMinute;
    private String timezone = "UTC";
}