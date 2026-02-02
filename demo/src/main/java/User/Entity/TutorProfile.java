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
    @GeneratedValue(strategy = GenerationType.UUID)
     String id;
     String userId;
    @OneToOne @JoinColumn(name = "userId", insertable = false, updatable = false)
     User user;

     String bio;
     String education;
     String[] certificates;
    @Column(columnDefinition = "jsonb")
    String proofDocuments;
     Integer yearsOfExperience = 0;
     Double hourlyRateMin;
     Double hourlyRateMax;
     String[] teachingModes;
     String city;
     String district;
     Boolean verified = false;
    @Enumerated(EnumType.STRING)  VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;
    @Column(unique = true)  String nationalIdNumber;
     String nationalIdFrontImageUrl;
     String nationalIdBackImageUrl;
    @Column(columnDefinition = "jsonb")  String certificatesDetail;
     LocalDateTime verificationSubmittedAt;
     Double trustScore = 0.0;
     Integer totalBookings = 0;
     Double averageRating = 0.0;

    @OneToMany(mappedBy = "tutor")
     List<Class> classes;
    @OneToMany(mappedBy = "tutor")
     List<TutorAvailability> availabilities;
}