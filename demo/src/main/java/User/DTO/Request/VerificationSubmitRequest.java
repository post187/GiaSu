package User.DTO.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerificationSubmitRequest {
    @NotBlank
    @Size(min = 6, max = 30)
    private String nationalIdNumber;

    @NotBlank
    @URL
    private String nationalIdFrontImageUrl;

    @NotBlank
    @URL
    private String nationalIdBackImageUrl;

    // Sử dụng Object hoặc Map cho kiểu 'any' trong Zod
    private Object proofDocuments;

    private Object certificatesDetail;
}
