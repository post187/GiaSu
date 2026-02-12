package Support.Service;

import Support.Dto.Response.CalendarItemResponse;

import java.util.List;

public interface CalendarService {
    List<CalendarItemResponse> getTutorCalendar(String userId);
    List<CalendarItemResponse> getStudentCalendar(String userId);
}
