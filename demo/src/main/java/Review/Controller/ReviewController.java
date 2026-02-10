package Review.Controller;


import Config.APIResponse;
import Review.Dto.Request.ReviewRequest;
import Review.Dto.Response.ReviewResponse;
import Review.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<APIResponse<ReviewResponse>> createReview(@Valid @RequestBody ReviewRequest request) {
        try {
            ReviewResponse response = reviewService.createReview(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(APIResponse.success(response));
        } catch (RuntimeException e) {
            // Mapping exception message to status code (Simple version)
            String msg = e.getMessage();
            if ("Student profile not found".equals(msg) || "Booking not completed".equals(msg) || "Booking already has review".equals(msg)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Hoặc trả về message lỗi
            } else if ("Booking not found".equals(msg)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            throw e;
        }
    }
}
