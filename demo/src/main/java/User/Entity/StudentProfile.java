package User.Entity;

import Booking.Entity.Booking;
import Notification.Entity.Review;
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
public class StudentProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    User user;

    String gradeLevel;

    String goals;

    @ElementCollection
    List<String> preferredSubject;

    private String notes;

    @CreationTimestamp
    LocalDateTime createdAt;

    @UpdateTimestamp
    LocalDateTime updatedAt;

    @OneToMany(mappedBy = "student")
    List<Booking> bookings;
    @OneToMany(mappedBy = "student")
    List<Review> reviews;
}
