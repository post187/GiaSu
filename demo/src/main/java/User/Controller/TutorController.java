package User.Controller;

import Config.APIResponse;
import User.DTO.Request.AvailabilityRequest;
import User.DTO.Request.TutorUpdateRequest;
import User.DTO.Response.AvailabilityResponse;
import User.DTO.Response.TrustScoreResponse;
import User.DTO.Response.TutorProfilePublicResponse;
import User.DTO.Response.TutorProfileResponse;
import User.Service.TutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/users/tutors")

public class TutorController {

    @Autowired
    private TutorService tutorService;

    // Lấy hồ sơ chính mình
    @GetMapping("/me")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<APIResponse<TutorProfileResponse>> getMyProfile() {
        return ResponseEntity.ok(APIResponse.success(tutorService.getMyProfile()));
    }

    // Cập nhật hồ sơ
    @PatchMapping("/me")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<APIResponse<TutorProfileResponse>> updateProfile(
            @RequestBody TutorUpdateRequest dto) {
        return ResponseEntity.ok(APIResponse.success(tutorService.updateProfile( dto)));
    }

    // Cập nhật lịch rảnh
    @PutMapping("/availability")
    @PreAuthorize("hasRole('TUTOR')")
    public ResponseEntity<APIResponse<List<AvailabilityResponse>>> updateAvailability(
            @RequestBody List<AvailabilityRequest> requests) {
        return ResponseEntity.ok(APIResponse.success(tutorService.updateAvailability(requests)));
    }

    // --- ENDPOINTS CÔNG KHAI (Học sinh/Khách xem) ---

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<TutorProfilePublicResponse>> getPublicProfile(@PathVariable String id) {
        return ResponseEntity.ok(APIResponse.success(tutorService.getPublicProfile(id)));
    }

    @GetMapping("/{id}/trust-score")
    public ResponseEntity<APIResponse<TrustScoreResponse>> getTrustScore(@PathVariable String id) {
        return ResponseEntity.ok(APIResponse.success(tutorService.getTutorTrustScore(id)));
    }
}