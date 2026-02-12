package Support.Controller;

import Config.APIResponse;
import Support.Dto.Request.MatchingRequest;
import Support.Dto.Request.TutorFilterRequest;
import Support.Dto.Response.TutorMatchResponse;
import Support.Service.MatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matching") // Prefix khác với /api/tutors nếu muốn tách biệt
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    /**
     * GET /api/matching/tutors
     * Legacy Filter: Tìm kiếm gia sư theo tiêu chí cơ bản
     */
    @GetMapping("/tutors")
    public ResponseEntity<APIResponse<List<TutorMatchResponse>>> searchTutors(
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String subjectId,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Double priceMin,
            @RequestParam(required = false) Double priceMax,
            @RequestParam(required = false) String gradeLevel
    ) {
        TutorFilterRequest request = new TutorFilterRequest();
        request.setStudentId(studentId);
        request.setSubjectId(subjectId);
        request.setCity(city);
        request.setDistrict(district);
        request.setPriceMin(priceMin);
        request.setPriceMax(priceMax);
        request.setGradeLevel(gradeLevel);

        return ResponseEntity.ok(APIResponse.success(matchingService.searchTutors(request)));
    }

    /**
     * POST /api/matching/tutors
     * Advanced Matching Engine
     */
    @PostMapping("/tutors")
    public ResponseEntity<APIResponse<List<TutorMatchResponse>>> matchTutors(
            @Valid @RequestBody MatchingRequest request
    ) {
        return ResponseEntity.ok(APIResponse.success(matchingService.matchTutors(request)));
    }
}
