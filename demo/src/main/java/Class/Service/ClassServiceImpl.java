package Class.Service;

import Booking.Dto.Response.BookingResponse;
import Booking.Entity.Booking;
import Booking.Entity.BookingStatus;
import Booking.Entity.CancelledBy;
import Booking.Repository.BookingRepository;
import Class.Dto.Request.*;
import Class.Dto.Response.*; // Import hết response
import Class.Entity.*;
import Class.Repository.ClassRepository;
import Class.Repository.ClassScheduleRepository;
import Class.Repository.SessionRepository;
import Notification.Repository.NotificationRepository; // Giả sử có repo này
import User.Entity.TutorProfile;
import User.Repository.TutorProfileRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.time.DayOfWeek;



@Service
@Slf4j
@RequiredArgsConstructor
public class ClassServiceImpl implements ClassService {

    private final ClassRepository classRepository;
    private final ClassScheduleRepository scheduleRepository;
    private final SessionRepository sessionRepository;
    private final BookingRepository bookingRepository;
    private final TutorProfileRepository tutorProfileRepository;
    private final ObjectMapper objectMapper; // Để xử lý JSON

    // --- 1. GET CLASSES ---
    @Override
    public Page<ClassResponse> getClasses(ClassFilterRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage() - 1,
                filter.getPageSize(),
                Sort.by("createdAt").descending()
        );
        ClassStatus statusQuery = filter.getStatus() != null ? filter.getStatus() : ClassStatus.PUBLISHED;

        return classRepository.searchClasses(
                filter.getTutorId(),
                filter.getSubjectId(),
                statusQuery,
                filter.getCity(),
                filter.getDistrict(),
                filter.getIncludeDeleted(),
                pageable
        ).map(this::mapToClassResponse);
    }

    // --- 2. GET CLASS BY ID ---
    @Override
    public ClassResponse getClassById(String id) {
        return mapToClassResponse(findClassOrThrow(id));
    }

    // --- 3. CREATE CLASS ---
    @Override
    @Transactional
    public ClassResponse createClass(String userId, ClassRequest request) {
        TutorProfile tutor = tutorProfileRepository.findByUserId(userId);

        Class.Entity.Class newClass = Class.Entity.Class.builder()
                .tutorId(tutor.getId())
                .subjectId(request.getSubjectId())
                .title(request.getTitle())
                .description(request.getDescription())
                .targetGrade(request.getTargetGrade())
                .pricePerHour(request.getPricePerHour())
                .locationType(request.getLocationType())
                .city(request.getCity())
                .district(request.getDistrict())
                .status(ClassStatus.DRAFT)
                .lifecycleStatus(ClassLifecycleStatus.PENDING)
                .build();

        return mapToClassResponse(classRepository.save(newClass));
    }

    // --- 4. UPDATE CLASS ---
    @Override
    @Transactional
    public ClassResponse updateClass(String id, String userId, UpdateClassRequest request) {
        Class.Entity.Class classEntity = getClassAndCheckOwner(id, userId);

        if (request.getTitle() != null) classEntity.setTitle(request.getTitle());
        if (request.getDescription() != null) classEntity.setDescription(request.getDescription());
        if (request.getPricePerHour() != null) classEntity.setPricePerHour(request.getPricePerHour());
        if (request.getTargetGrade() != null) classEntity.setTargetGrade(request.getTargetGrade());
        if (request.getLocationType() != null) classEntity.setLocationType(request.getLocationType());
        if (request.getCity() != null) classEntity.setCity(request.getCity());
        if (request.getDistrict() != null) classEntity.setDistrict(request.getDistrict());

        return mapToClassResponse(classRepository.save(classEntity));
    }

    // --- 5. UPDATE STATUS ---
    @Override
    @Transactional
    public ClassResponse updateClassStatus(String id, String userId, ClassStatus status) {
        Class.Entity.Class classEntity = getClassAndCheckOwner(id, userId);
        classEntity.setStatus(status);
        return mapToClassResponse(classRepository.save(classEntity));
    }

    // --- 6. CANCEL CLASS ---
    @Override
    @Transactional
    public void cancelClass(String id, String userId, String reason) {
        Class.Entity.Class classEntity = getClassAndCheckOwner(id, userId);

        if (classEntity.getLifecycleStatus() == ClassLifecycleStatus.CANCELLED) {
            throw new RuntimeException("Class is already cancelled");
        }

        classEntity.setLifecycleStatus(ClassLifecycleStatus.CANCELLED);
        classEntity.setStatus(ClassStatus.ARCHIVED);
        classRepository.save(classEntity);

        List<Booking> bookings = bookingRepository.findActiveBookingsByClassId(id);
        for (Booking booking : bookings) {
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setCancelReason(reason);
            booking.setCancelledBy(CancelledBy.TUTOR);
            bookingRepository.save(booking);
            // TODO: Call notification service
        }
    }

    // --- 7. DELETE CLASS ---
    @Override
    @Transactional
    public void deleteClass(String id, String userId) {
        Class.Entity.Class classEntity = getClassAndCheckOwner(id, userId);

        if (bookingRepository.countActiveBookings(id) > 0) {
            throw new RuntimeException("Cannot delete class with active bookings");
        }

        classEntity.setIsDeleted(true);
        classEntity.setStatus(ClassStatus.ARCHIVED);
        classRepository.save(classEntity);
    }

    // --- 8. GET STUDENTS ---
    @Override
    public List<BookingResponse> getClassStudents(String id, String userId) {
        getClassAndCheckOwner(id, userId);
        return bookingRepository.findActiveBookingsByClassId(id).stream()
                .map(this::mapToBookingResponse)
                .collect(Collectors.toList());
    }

    // --- 9. GET SESSIONS ---
    @Override
    public ClassDetailResponse getClassSessions(String id, String userId, String userRole) {
        Class.Entity.Class classEntity = findClassOrThrow(id);

        boolean isOwner = isTutorOwner(classEntity, userId);
        if (!isOwner) {
            boolean isStudent = bookingRepository.existsByClassIdAndUserIdAndStatus(id, userId, BookingStatus.CONFIRMED);
            if (!isStudent && !"ADMIN".equals(userRole)) {
                throw new RuntimeException("Unauthorized");
            }
        }

        List<Session> sessions = sessionRepository.findByClassIdOrderByScheduledStartAtAsc(id);

        return ClassDetailResponse.builder()
                .classInfo(mapToClassResponse(classEntity))
                .sessions(sessions.stream().map(this::mapToSessionResponse).collect(Collectors.toList()))
                .build();
    }

    // --- 10. GET SCHEDULE ---
    @Override
    public ClassScheduleResponse getClassSchedule(String id) {
        ClassSchedule schedule = scheduleRepository.findByClassId(id).orElse(null);
        return mapToScheduleResponse(schedule);
    }

    // --- 11. CLEAR SCHEDULE ---
    @Override
    @Transactional
    public void clearSchedule(String id, String userId) {
        getClassAndCheckOwner(id, userId);
        sessionRepository.deleteFutureSessions(id);
        scheduleRepository.deleteByClassId(id);

        Class.Entity.Class clazz = findClassOrThrow(id);
        clazz.setTotalSessions((int) sessionRepository.countByClassIdAndStatus(id, SessionStatus.COMPLETED));
        classRepository.save(clazz);
    }

    // --- 12. UPDATE SCHEDULE (Logic tạo Sessions) ---
    @Override
    @Transactional
    public ClassScheduleResponse updateSchedule(String id, String userId, ScheduleRequest request) {
        Class.Entity.Class classEntity = getClassAndCheckOwner(id, userId);

        // 1. Xóa session tương lai
        sessionRepository.deleteFutureSessions(id);

        // 2. Tạo Sessions mới
        List<Session> newSessions = new ArrayList<>();

        // 2a. Xử lý Recurrence
        if (request.getRecurrence() != null) {
            LocalDate startDate = LocalDate.parse(request.getRecurrence().getStartDate());
            int weeks = request.getRecurrence().getWeeks();

            for (int w = 0; w < weeks; w++) {
                LocalDate weekStart = startDate.plusWeeks(w);
                for (SlotDto slot : request.getRecurrence().getSlots()) {
                    // Tính ngày dựa trên DayOfWeek (1=Mon ... 7=Sun)
                    LocalDate sessionDate = weekStart.with(TemporalAdjusters.nextOrSame(
                            DayOfWeek.of(slot.getDayOfWeek())
                    ));

                    LocalDateTime start = sessionDate.atTime(LocalTime.ofSecondOfDay(slot.getStartMinute() * 60));
                    LocalDateTime end = sessionDate.atTime(LocalTime.ofSecondOfDay(slot.getEndMinute() * 60));

                    newSessions.add(Session.builder()
                            .classId(id)
                            .scheduledStartAt(start)
                            .scheduledEndAt(end)
                            .status(SessionStatus.SCHEDULED)
                            .build());
                }
            }
        }

        // 2b. Xử lý Explicit Sessions
        if (request.getExplicitSessions() != null) {
            for (TimeRangeDto es : request.getExplicitSessions()) {
                newSessions.add(Session.builder()
                        .classId(id)
                        .scheduledStartAt(es.getStartAt())
                        .scheduledEndAt(es.getEndAt())
                        .status(SessionStatus.SCHEDULED)
                        .build());
            }
        }

        // 3. Lưu Schedule Config (JSON)
        ClassSchedule schedule = scheduleRepository.findByClassId(id).orElse(new ClassSchedule());
        schedule.setClassId(id);
        schedule.setTimezone(request.getTimezone());
        try {
            schedule.setRecurrenceRule(objectMapper.writeValueAsString(request.getRecurrence()));
            schedule.setExplicitSessions(objectMapper.writeValueAsString(request.getExplicitSessions()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON Error", e);
        }
        scheduleRepository.save(schedule);

        // 4. Lưu Sessions & Update Class
        sessionRepository.saveAll(newSessions);
        classEntity.setTotalSessions((int) sessionRepository.countByClassId(id));
        classRepository.save(classEntity);

        return mapToScheduleResponse(schedule);
    }

    // ================= HELPERS & MAPPERS =================

    private Class.Entity.Class findClassOrThrow(String id) {
        return classRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found"));
    }

    private Class.Entity.Class getClassAndCheckOwner(String id, String userId) {
        Class.Entity.Class c = findClassOrThrow(id);
        if (!isTutorOwner(c, userId)) throw new RuntimeException("Forbidden");
        return c;
    }

    private boolean isTutorOwner(Class.Entity.Class classEntity, String userId) {
        TutorProfile tutor = tutorProfileRepository.findByUserId(userId);

        if (tutor == null) {
            return false;
        }

        return tutor.getId().equals(classEntity.getTutorId());
    }

    private ClassResponse mapToClassResponse(Class.Entity.Class e) {
        return ClassResponse.builder()
                .id(e.getId())
                .title(e.getTitle())
                .description(e.getDescription())
                .status(e.getStatus())
                .lifecycleStatus(e.getLifecycleStatus())
                .pricePerHour(e.getPricePerHour())
                .city(e.getCity())
                .district(e.getDistrict())
                .createdAt(e.getCreatedAt())
                .build();
    }

    private BookingResponse mapToBookingResponse(Booking e) {
        BookingResponse.BookingResponseBuilder b = BookingResponse.builder()
                .id(e.getId())
                .classId(e.getClassId())
                .status(e.getStatus())
                .isTrial(e.getIsTrial())
                .createdAt(e.getCreatedAt());

        // SỬA LỖI: Dùng getClazz() thay vì getClassInfo()
        if (e.getClazz() != null) {
            b.className(e.getClazz().getName());
        }
        if (e.getStudent() != null) {
            b.studentName(e.getStudent().getUser().getFullName());

        }
        if (e.getTutor() != null) {
            b.tutorName(e.getTutor().getUser().getFullName());
        }
        return b.build();
    }

    private SessionResponse mapToSessionResponse(Session e) {
        return SessionResponse.builder()
                .id(e.getId())
                .startAt(String.valueOf(e.getScheduledStartAt()))
                .endAt(String.valueOf(e.getScheduledEndAt()))
                .status(String.valueOf(e.getStatus()))
                .build();
    }

    private ClassScheduleResponse mapToScheduleResponse(ClassSchedule e) {
        if (e == null) return null;
        try {
            // Convert JSON String ngược lại thành Object để trả về FE
            return ClassScheduleResponse.builder()
                    .scheduleId(e.getId())
                    .timezone(e.getTimezone())
                    .recurrenceRule(e.getRecurrenceRule() != null ?
                            objectMapper.readValue(e.getRecurrenceRule(), Object.class) : null)
                    .explicitSessions(e.getExplicitSessions() != null ?
                            objectMapper.readValue(e.getExplicitSessions(), Object.class) : null)
                    .build();
        } catch (JsonProcessingException ex) {
            log.error("Error parsing schedule JSON", ex);
            return ClassScheduleResponse.builder().scheduleId(e.getId()).build();
        }
    }
}