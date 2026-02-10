package User.Service;

import Class.Repository.ClassRepository;
import Notification.Service.NotificationService;
import User.DTO.Request.ClassFilterRequest;
import User.DTO.Response.ClassAdminResponse;
import User.DTO.Response.TutorPendingResponse;
import User.Entity.Status;
import User.Entity.TutorProfile;
import User.Entity.User;
import User.Entity.VerificationStatus;
import User.Repository.TutorProfileRepository;
import User.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private TutorProfileRepository tutorProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassRepository classRepository;


    @Autowired
    private NotificationService notificationService; // Để gửi thông báo giống createNotification trong file .ts

    @Override
    public List<TutorPendingResponse> getPendingTutors() {
        return tutorProfileRepository.findPendingTutors().stream()
                .map(tutor -> TutorPendingResponse.builder()
                        .id(tutor.getId())
                        .fullName(tutor.getUser().getFullName())
                        .email(tutor.getUser().getEmail())
                        .nationalIdNumber(maskId(tutor.getNationalIdNumber())) // Logic mask số CCCD
                        .nationalIdFrontImageUrl(tutor.getNationalIdFrontImageUrl())
                        .nationalIdBackImageUrl(tutor.getNationalIdBackImageUrl())
                        .verificationSubmittedAt(tutor.getVerificationSubmittedAt())
                        .verificationStatus(tutor.getVerificationStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveTutor(String tutorId) {
        TutorProfile tutor = tutorProfileRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        // Cập nhật trạng thái Profile
        tutor.setVerificationStatus(VerificationStatus.VERIFIED);
        tutor.setVerified(true);
        tutor.setVerificationReviewedAt(LocalDateTime.now());

        // Cập nhật trạng thái User thành ACTIVE
        User user = tutor.getUser();
        user.setStatus(Status.ACTIVE);

        tutorProfileRepository.save(tutor);
        userRepository.save(user);

        // Gửi thông báo
        notificationService.createNotification(user.getId(), "Hồ sơ gia sư của bạn đã được phê duyệt.");
    }

    @Override
    @Transactional
    public void rejectTutor(String tutorId, String note) {
        TutorProfile tutor = tutorProfileRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        tutor.setVerificationStatus(VerificationStatus.REJECTED);
        tutor.setVerificationNotes(note);
        tutor.setVerificationReviewedAt(LocalDateTime.now());

        tutorProfileRepository.save(tutor);

        // Gửi thông báo kèm lý do
        notificationService.createNotification(tutor.getUser().getId(), "Hồ sơ gia sư của bạn bị từ chối: " + note);
    }


    private ClassAdminResponse mapToAdminResponse(Class.Entity.Class entity) {
        if (entity == null) return null;

        // Xử lý null safety cho các quan hệ Lazy
        String tutorName = "N/A";
        if (entity.getTutor() != null && entity.getTutor().getUser() != null) {
            tutorName = entity.getTutor().getUser().getFullName();
        }

        String subjectName = "N/A";
        if (entity.getSubject() != null) {
            subjectName = entity.getSubject().getName();
        }

        return ClassAdminResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .targetGrade(entity.getTargetGrade())
                .pricePerHour(entity.getPricePerHour())
                .locationType(entity.getLocationType())
                .city(entity.getCity())
                .district(entity.getDistrict())
                .status(entity.getStatus())
                .lifecycleStatus(entity.getLifecycleStatus())
                .createdAt(entity.getCreatedAt())
                .tutorName(tutorName)     // Dữ liệu đã có nhờ @ManyToOne
                .subjectName(subjectName) // Dữ liệu đã có nhờ @ManyToOne
                .build();
    }

    // Hàm phụ trợ để ẩn số CCCD (giống maskNationalId trong utils)
    private String maskId(String id) {
        if (id == null || id.length() < 4) return "****";
        return id.substring(0, 2) + "xxxx" + id.substring(id.length() - 2);
    }
}