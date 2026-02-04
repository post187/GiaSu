package User.DTO.Response;

import Class.Entity.ClassLifecycleStatus;
import Class.Entity.ClassStatus;
import Class.Entity.LocationType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassAdminResponse {
    String id;
    String title;
    String description;
    String targetGrade;
    Double pricePerHour; // Đổi tên từ fee cho khớp Entity
    LocationType locationType;
    String city;
    String district;
    ClassStatus status;
    ClassLifecycleStatus lifecycleStatus;
    LocalDateTime createdAt;

    // Thông tin bổ sung từ các bảng quan hệ
    String tutorName;
    String subjectName;
}
