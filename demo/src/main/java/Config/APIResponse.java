package Config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {
    private int status;
    private String message;
    private T result;

    public APIResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(200, "success", data);
    }

    public static <T> APIResponse<T> success(String message, T data) {
        return new APIResponse<>(200, message, data);
    }

    public static <T> APIResponse<T> error (int status, String message) {
        return new APIResponse<>(status, message);
    }

}
