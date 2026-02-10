package Subject.Controller;

import Config.APIResponse;
import Subject.Dto.Request.SubjectRequest;
import Subject.Dto.Response.SubjectResponse;
import Subject.Service.SubjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subject")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * GET /api/subjects
     * Public access
     * Returns list of subjects sorted by level and name
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<SubjectResponse>>> getSubjects() {
        return ResponseEntity.ok(APIResponse.success(subjectService.getAllSubjects()));
    }

    /**
     * POST /api/subjects
     * Restricted to ADMIN role only
     * Creates a new subject
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')") // Tương đương authGuard([UserRole.ADMIN])
    public ResponseEntity<APIResponse<SubjectResponse>> createSubject(@Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(response));
    }
}