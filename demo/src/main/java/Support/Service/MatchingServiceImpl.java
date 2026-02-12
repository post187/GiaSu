package Support.Service;

import Class.Entity.ClassStatus;
import Support.Dto.Request.MatchingRequest;
import Support.Dto.Request.TutorFilterRequest;
import Support.Dto.Response.TutorMatchResponse;
import User.DTO.Response.TutorProfileResponse;
import User.Entity.StudentProfile;
import User.Entity.TutorProfile;
import User.Entity.VerificationStatus;
import User.Repository.StudentProfileRepository;
import User.Repository.TutorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final TutorProfileRepository tutorRepository;
    private final StudentProfileRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TutorMatchResponse> searchTutors(TutorFilterRequest req) {
        // 1. Resolve Student Preferences
        String effectiveSubjectId = req.getSubjectId();
        if (req.getStudentId() != null) {
            StudentProfile student = studentRepository.findById(req.getStudentId()).orElse(null);
            if (student != null && effectiveSubjectId == null && !student.getPreferredSubject().isEmpty()) {
                // Lấy subject đầu tiên trong list preferences (giả lập logic TS)
                // Lưu ý: preferredSubjects trong Entity là String[] hoặc List<String>
                // effectiveSubjectId = student.getPreferredSubjects().get(0);
            }
        }

        // 2. Fetch Potential Tutors (Hard Filters: City, District, Verified)
        List<TutorProfile> candidates = tutorRepository.findPotentialTutors(
                VerificationStatus.VERIFIED,
                req.getCity(),
                req.getDistrict()
        );

        // 3. Score & Filter In-Memory
        String finalSubjectId = effectiveSubjectId;
        return candidates.stream()
                .map(tutor -> calculateMatch(tutor, req, finalSubjectId))
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore())) // Sort Descending
                .limit(20)
                .collect(Collectors.toList());
    }

    @Override
    public List<TutorMatchResponse> matchTutors(MatchingRequest request) {
        // Đây là nơi gọi "Matching Engine" phức tạp.
        // Trong phạm vi file TS, nó import `matchTutors` từ domain/matchingEngine.
        // Ở đây mình sẽ giả lập một logic cơ bản tương tự hàm search nhưng dùng budget.

        // Convert MatchingRequest sang FilterRequest để tái sử dụng logic cơ bản
        TutorFilterRequest filter = new TutorFilterRequest();
        filter.setSubjectId(request.getSubjectId());
        filter.setGradeLevel(request.getGradeLevel());
        filter.setCity(request.getCity());
        filter.setDistrict(request.getDistrict());
        // Budget logic có thể phức tạp hơn (VD: +/- 20%)
        filter.setPriceMax(request.getBudgetPerHour() * 1.2);

        List<TutorMatchResponse> matches = searchTutors(filter);

        // Cắt theo limit của request
        return matches.stream().limit(request.getLimit()).collect(Collectors.toList());
    }

    // --- HELPER: Scoring Logic (Ported from TS) ---
    private TutorMatchResponse calculateMatch(TutorProfile tutor, TutorFilterRequest req, String subjectId) {
        // Filter Classes của Tutor này xem có khớp Subject/Grade/Price không
        // Lưu ý: tutor.getClasses() đã được fetch eager hoặc lazy (trong transaction)

        boolean subjectMatch = false;
        boolean gradeMatch = false;

        if (tutor.getClasses() != null) {
            subjectMatch = subjectId != null && tutor.getClasses().stream()
                    .anyMatch(c -> c.getSubjectId().equals(subjectId) && c.getStatus() == ClassStatus.PUBLISHED);

            gradeMatch = req.getGradeLevel() != null && tutor.getClasses().stream()
                    .anyMatch(c -> c.getTargetGrade() != null &&
                            c.getTargetGrade().toLowerCase().contains(req.getGradeLevel().toLowerCase()));
        }

        // Calculate Price Penalty
        double pricePenalty = 0;
        if (req.getPriceMin() != null || req.getPriceMax() != null) {
            double min = req.getPriceMin() != null ? req.getPriceMin() : Double.NEGATIVE_INFINITY;
            double max = req.getPriceMax() != null ? req.getPriceMax() : Double.POSITIVE_INFINITY;

            Double closestPrice = null;
            if (tutor.getClasses() != null) {
                for (Class.Entity.Class cls : tutor.getClasses()) {
                    if (cls.getPricePerHour() == null) continue;
                    double price = cls.getPricePerHour();

                    if (closestPrice == null) {
                        closestPrice = price;
                    } else {
                        // Tìm giá gần khoảng range nhất
                        double distCurrent = Math.min(Math.abs(price - min), Math.abs(price - max));
                        double distClosest = Math.min(Math.abs(closestPrice - min), Math.abs(closestPrice - max));

                        // Logic đơn giản hóa: Nếu nằm trong range thì dist = 0
                        if (price >= min && price <= max) distCurrent = 0;
                        if (closestPrice >= min && closestPrice <= max) distClosest = 0;

                        if (distCurrent < distClosest) closestPrice = price;
                    }
                }
            }

            if (closestPrice != null) {
                if (closestPrice < min) pricePenalty += (min - closestPrice);
                if (closestPrice > max) pricePenalty += (closestPrice - max);
            }
        }

        // Calculate Score formula
        // tutor.trustScore * 0.5 + tutor.averageRating * 10 + ...
        double score = (tutor.getTrustScore() * 0.5) +
                (tutor.getAverageRating() * 10) +
                (tutor.getTotalCompletedBookings() * 2) +
                (subjectMatch ? 6 : 0) +
                (gradeMatch ? 4 : 0) -
                pricePenalty;

        return TutorMatchResponse.builder()
                .tutor(mapToTutorResponse(tutor)) // Cần hàm map và sanitize
                .matchScore(score)
                .build();
    }

    private TutorProfileResponse mapToTutorResponse(TutorProfile entity) {
        // Map và Mask National ID (Sanitizer)
        return TutorProfileResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                // .fullName(entity.getUser().getFullName()) // Cần join User
                .bio(entity.getBio())
                .trustScore(entity.getTrustScore())
                .averageRating(entity.getAverageRating())
                // Mask ID
                .nationalIdNumber(entity.getNationalIdNumber() != null ? "******" : null)
                .build();
    }
}