package Review.Entity;

import Booking.Entity.Booking;
import User.Entity.StudentProfile;
import User.Entity.TutorProfile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_tutor", columnList = "tutorId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @UuidGenerator
    private String id;

    @Column(nullable = false, unique = true) // One review per booking
    private String bookingId;

    @Column(nullable = false)
    private String studentId;

    @Column(nullable = false)
    private String tutorId;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Relations
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookingId", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studentId", insertable = false, updatable = false)
    private StudentProfile student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutorId", insertable = false, updatable = false)
    private TutorProfile tutor;
}