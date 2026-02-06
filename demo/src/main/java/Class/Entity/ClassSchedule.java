package Class.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "class_schedules")
public class ClassSchedule {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String classId;

    @OneToOne
    @JoinColumn(name = "classId", insertable = false, updatable = false)
    private Class clazz;

    private String timezone = "UTC";

    @Column(columnDefinition = "TEXT") // Map to Json in Prisma
    private String recurrenceRule;

    @Column(columnDefinition = "TEXT") // Map to Json in Prisma
    private String explicitSessions;

    private Integer totalSessions;
}