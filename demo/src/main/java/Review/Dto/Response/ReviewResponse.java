package Review.Dto.Response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private String id;
    private String bookingId;
    private String studentId;
    private String tutorId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
