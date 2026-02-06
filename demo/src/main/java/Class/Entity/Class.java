package Class.Entity;

import Subject.Entity.Subject;
import User.Entity.TutorProfile;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "classes")
public class Class {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String tutorId;
    private String subjectId;
    private String title;
    private String description;
    private String targetGrade;
    private Double pricePerHour;

    @Enumerated(EnumType.STRING) private LocationType locationType;
    private String city;
    private String district;

    @Enumerated(EnumType.STRING) private ClassStatus status = ClassStatus.DRAFT;
    @Enumerated(EnumType.STRING) private ClassLifecycleStatus lifecycleStatus = ClassLifecycleStatus.PENDING;

    private Integer totalSessions = 0;
    private Integer sessionsCompleted = 0;
    private Boolean isDeleted = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "clazz", cascade = CascadeType.ALL)
    private ClassSchedule schedule;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }}