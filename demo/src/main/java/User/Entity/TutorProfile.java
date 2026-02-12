package User.Entity;

import Booking.Entity.Booking;
import Config.JsonConverter;
import Review.Entity.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tutor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String education;

    @ElementCollection
    @CollectionTable(name = "tutor_certificates", joinColumns = @JoinColumn(name = "tutor_id"))
    @Column(name = "certificate")
    private List<String> certificates = new ArrayList<>();

    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private String proofDocuments;

    private Integer yearsOfExperience = 0;

    private Double hourlyRateMin;

    private Double hourlyRateMax;

    @ElementCollection
    @CollectionTable(name = "tutor_teaching_modes", joinColumns = @JoinColumn(name = "tutor_id"))
    @Column(name = "mode")
    private List<String> teachingModes = new ArrayList<>();

    private String city;

    private String district;

    private Boolean verified = false;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(unique = true)
    private String nationalIdNumber;

    private String nationalIdFrontImageUrl;

    private String nationalIdBackImageUrl;

    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private String certificatesDetail;

    private LocalDateTime verificationSubmittedAt;

    private LocalDateTime verificationReviewedAt;

    @Column(columnDefinition = "TEXT")
    private String verificationNotes;

    // --- Các trường Trust Score & Thống kê ---
    private Double trustScore = 0.0;

    private Integer totalBookings = 0;

    private Double averageRating = 0.0;

    private Integer totalCancelledBookings = 0;

    private Integer totalCompletedBookings = 0;

    private Integer totalReviews = 0;

    private Integer policyViolationsCount = 0;

    private Integer avgResponseTimeSeconds = 0;

    private LocalDateTime lastTrustScoreUpdatedAt = LocalDateTime.now();

    // --- AI Embeddings ---
    @Column(columnDefinition = "json")
    @Convert(converter = JsonConverter.class)
    private String profileEmbedding;

    private String profileEmbeddingModel;

    @Column(columnDefinition = "TEXT")
    private String moderationNote;

    // --- Timestamps ---
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Relationships ---
    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<Class.Entity.Class> classes;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<Booking> bookings;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "tutor", cascade = CascadeType.ALL)
    private List<TutorAvailability> availabilities;
}