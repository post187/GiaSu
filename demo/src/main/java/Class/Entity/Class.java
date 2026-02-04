package Class.Entity;

import Subject.Entity.Subject;
import User.Entity.TutorProfile;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "classes")
public class Class {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
     String id;
     String tutorId;
     String subjectId;

    @ManyToOne @JoinColumn(name = "tutorId", insertable = false, updatable = false)
     TutorProfile tutor;
    @ManyToOne @JoinColumn(name = "subjectId", insertable = false, updatable = false)
     Subject subject;

     String title;
     String description;
     String targetGrade;
     Double pricePerHour;
    @Enumerated(EnumType.STRING)  LocationType locationType;
     String city;
     String district;
    @Enumerated(EnumType.STRING)  ClassStatus status = ClassStatus.DRAFT;
    @Enumerated(EnumType.STRING)  ClassLifecycleStatus lifecycleStatus = ClassLifecycleStatus.PENDING;

    @OneToOne(mappedBy = "clazz")  ClassSchedule schedule;
    @OneToMany(mappedBy = "clazz")  List<Session> sessions;

    LocalDateTime createAt;
}