package Class.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class ClassSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String classId;
    @OneToOne
    @JoinColumn(name = "classId", insertable = false, updatable = false)
    private Class clazz;

    private String timezone = "UTC";
    @Column(columnDefinition = "jsonb") private String recurrenceRule;
    @Column(columnDefinition = "jsonb") private String explicitSessions;
    private Integer totalSessions;
}