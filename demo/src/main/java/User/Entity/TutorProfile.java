package User.Entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TutorProfile {

    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    User user;

    String bio;

    String education;

    @ElementCollection
    private List<String> certificates;

    private int yearsOfExperience;

    private Float hourlyRateMin;
    private Float hourlyRateMax;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    private boolean verified;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
