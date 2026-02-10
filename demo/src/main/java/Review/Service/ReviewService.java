package Review.Service;

import Review.Dto.Request.ReviewRequest;
import Review.Dto.Response.ReviewResponse;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request);
}
