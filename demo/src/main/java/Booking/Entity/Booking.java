package Booking.Entity;

import Notification.Entity.Review;
import User.Entity.StudentProfile;
import User.Entity.TutorProfile;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String classId;
    private String studentId;
    private String tutorId;

    @ManyToOne
    @JoinColumn(name = "classId", insertable = false, updatable = false)
    private Class clazz;

    @ManyToOne
    @JoinColumn(name = "studentId", insertable = false, updatable = false)
    private StudentProfile student;

    @ManyToOne
    @JoinColumn(name = "tutorId", insertable = false, updatable = false)
    private TutorProfile tutor;

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;

    private Boolean isTrial = false;
    private Integer requestedHoursPerWeek;
    private LocalDateTime startDateExpected;

    @Column(columnDefinition = "TEXT")
    private String noteFromStudent;

    // --- CÁC TRƯỜNG HỦY NẰM Ở ĐÂY ---
    @Column(columnDefinition = "TEXT")
    private String cancelReason;

    @Enumerated(EnumType.STRING)
    private CancelledBy cancelledBy; // Enum: STUDENT, TUTOR, SYSTEM
    // ------------------------------

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "booking")
    private Review review;
}