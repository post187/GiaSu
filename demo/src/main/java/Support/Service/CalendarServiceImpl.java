package Support.Service;

import Booking.Entity.Booking;
import Booking.Entity.BookingStatus;
import Booking.Repository.BookingRepository;
import Support.Dto.Response.CalendarItemResponse;
import User.Entity.StudentProfile;
import User.Entity.TutorProfile;
import User.Repository.StudentProfileRepository;
import User.Repository.TutorProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalendarServiceImpl implements CalendarService {

    private final BookingRepository bookingRepository;
    private final TutorProfileRepository tutorRepository;
    private final StudentProfileRepository studentRepository;
    private final ObjectMapper objectMapper; // Jackson để parse JSON


    private static final List<BookingStatus> ACTIVE_STATUSES = Arrays.asList(
            BookingStatus.CONFIRMED,
            BookingStatus.TRIAL
    );

    @Override
    @Transactional(readOnly = true)
    public List<CalendarItemResponse> getTutorCalendar(String userId) {
        TutorProfile tutor = tutorRepository.findByUserId(userId);

        List<Booking> bookings = bookingRepository.findActiveBookingsForTutor(tutor.getId(), ACTIVE_STATUSES);
        return processBookings(bookings, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CalendarItemResponse> getStudentCalendar(String userId) {
        StudentProfile student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        List<Booking> bookings = bookingRepository.findActiveBookingsForStudent(student.getId(), ACTIVE_STATUSES);
        return processBookings(bookings, false);
    }

    // --- Helper Logic ---

    private List<CalendarItemResponse> processBookings(List<Booking> bookings, boolean isTutorView) {
        List<CalendarItemResponse> scheduleList = new ArrayList<>();

        for (Booking booking : bookings) {
            if (booking.getNoteFromStudent() == null) continue;

            try {
                // Parse JSON: {"preferredSlot": {"dayOfWeek": 1, "startTime": "14:00", "endTime": "16:00"}}
                JsonNode root = objectMapper.readTree(booking.getNoteFromStudent());
                JsonNode slot = root.get("preferredSlot");

                if (slot != null && !slot.isMissingNode()) {
                    CalendarItemResponse builder = CalendarItemResponse.builder()
                            .id(booking.getId())
                            .subjectName(booking.getClazz().getSubject().getName())
                            .className(booking.getClazz().getTitle())
                            .dayOfWeek(slot.get("dayOfWeek").asInt())
                            .startTime(slot.get("startTime").asText())
                            .endTime(slot.get("endTime").asText())
                            .status(booking.getStatus())
                            .locationType(booking.getClazz().getLocationType()).build();

                    if (isTutorView) {
                        builder.setStudentName(booking.getStudent().getUser().getFullName());
                    } else {
                        // Student xem tên gia sư
                        builder.setTutorName(booking.getClazz().getTutor().getUser().getFullName());
                    }

                    scheduleList.add(builder);
                }
            } catch (Exception e) {
                // Ignore parsing errors, skip this item similar to TS logic
                log.debug("Failed to parse booking note for calendar: {}", booking.getId());
            }
        }

        // Sort: DayOfWeek (asc) -> StartTime (asc)
        scheduleList.sort(Comparator.comparingInt(CalendarItemResponse::getDayOfWeek)
                .thenComparing(CalendarItemResponse::getStartTime));

        return scheduleList;
    }
}