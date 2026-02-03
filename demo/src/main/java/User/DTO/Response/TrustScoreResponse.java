package User.DTO.Response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrustScoreResponse {
    private Double averageRating;          // Điểm đánh giá trung bình (ví dụ: 4.8)
    private Double trustScore;            // Điểm uy tín tổng hợp (ví dụ: 95/100)
    private Integer totalReviews;          // Tổng số lượt nhận xét
    private Integer totalCompletedBookings; // Tổng số lớp học đã hoàn thành
}