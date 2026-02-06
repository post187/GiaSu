package Class.Dto.Request;

import Class.Entity.ClassStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClassStatusRequest {
    @NotNull
    private ClassStatus status;
}
