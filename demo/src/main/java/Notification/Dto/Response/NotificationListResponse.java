package Notification.Dto.Response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class NotificationListResponse {
    private List<NotificationResponse> data;
    private long total;
    private int page;
    private int pageSize;
    private long unread;
}
