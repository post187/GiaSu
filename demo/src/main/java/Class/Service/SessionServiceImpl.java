package Class.Service;

import Booking.Entity.BookingStatus;
import Class.Dto.Response.SessionResponse;
import Class.Entity.ClassLifecycleStatus;
import Class.Entity.Session;
import Class.Entity.SessionStatus;
import Class.Repository.SessionRepository;
import Notification.Service.NotificationService;
import User.Entity.Role;
import User.Entity.StudentProfile;
import User.Entity.TutorProfile;
import User.Entity.VerificationStatus;
import User.Repository.StudentProfileRepository;
import User.Repository.TutorProfileRepository;
import User.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;
    private final TutorProfileRepository tutorRepository;
    private final StudentProfileRepository studentRepository;
    private final UserRepository userRepository; // Dùng để tìm Admin
    private final NotificationService notificationService;


    private static final int START_WINDOW_MINUTES_BEFORE = 15;
    private static final int START_WINDOW_MINUTES_AFTER = 60;
    private static final int DISPUTE_HOURS = 6;

    // --- 1. START SESSION ---
    @Override
    @Transactional
    public SessionResponse startSession(String sessionId, String userId) {
        Session session = sessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 1. Check Permission & Actor Role
        Role role = ensureActorAllowed(session, userId);

        // 2. Check Time Window
        if (!withinStartWindow(session)) {
            throw new RuntimeException("Outside start window");
        }

        LocalDateTime now = LocalDateTime.now();

        // 3. Update Confirmations
        if (role == Role.TEACHER) {
            if (session.getTutorStartConfirmedAt() == null) session.setTutorStartConfirmedAt(now);
        } else {
            if (session.getStudentStartConfirmedAt() == null) session.setStudentStartConfirmedAt(now);
        }

        // 4. Check if both confirmed
        boolean tutorConfirmed = session.getTutorStartConfirmedAt() != null;
        boolean studentConfirmed = session.getStudentStartConfirmedAt() != null;

        if (tutorConfirmed && studentConfirmed) {
            session.setStatus(SessionStatus.IN_PROGRESS);
            if (session.getStartedAt() == null) session.setStartedAt(now);
        }

        // 5. Flag Dispute Logic
        maybeFlagDispute(session, now);

        Session updated = sessionRepository.save(session);

        // 6. Notifications
        sendStartNotifications(updated, userId, role, tutorConfirmed, studentConfirmed);

        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public SessionResponse completeSession(String sessionId, String userId) {
        Session session = sessionRepository.findByIdWithDetails(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Role role = ensureActorAllowed(session, userId);

        if (!canComplete(session)) {
            throw new RuntimeException("Cannot complete yet");
        }

        LocalDateTime now = LocalDateTime.now();

        // 1. Update Confirmations
        if (role == Role.TEACHER) {
            if (session.getTutorCompleteConfirmedAt() == null) session.setTutorCompleteConfirmedAt(now);
        } else {
            if (session.getStudentCompleteConfirmedAt() == null) session.setStudentCompleteConfirmedAt(now);
        }

        boolean tutorConfirmed = session.getTutorCompleteConfirmedAt() != null;
        boolean studentConfirmed = session.getStudentCompleteConfirmedAt() != null;
        boolean shouldComplete = tutorConfirmed && studentConfirmed;

        // 2. Flag Dispute Logic
        maybeFlagDispute(session, now);

        // 3. State Transition
        if (shouldComplete && session.getStatus() != SessionStatus.COMPLETED) {
            session.setStatus(SessionStatus.COMPLETED);
            if (session.getCompletedAt() == null) session.setCompletedAt(now);

            // Update Class Stats
            Class.Entity.Class clazz = session.getClazz();
            clazz.setSessionsCompleted(clazz.getSessionsCompleted() + 1);

            // Update Lifecycle if all done
            if (clazz.getTotalSessions() > 0 && clazz.getSessionsCompleted() >= clazz.getTotalSessions()) {
                clazz.setLifecycleStatus(ClassLifecycleStatus.COMPLETED);
            }
        }

        Session updated = sessionRepository.save(session);

        // 4. Notifications
        sendCompleteNotifications(updated, userId, role, tutorConfirmed, studentConfirmed);


        return mapToResponse(updated);
    }

    // ================= HELPER LOGIC =================


    private Role ensureActorAllowed(Session session, String userId) {
        Class.Entity.Class clazz = session.getClazz();

        // Check Tutor
        TutorProfile tutor = tutorRepository.findByUserId(userId);
        if (tutor != null && tutor.getId().equals(clazz.getTutorId())) {
            if (tutor.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new RuntimeException("Tutor not verified");
            }
            return Role.TEACHER;
        }

        // Check Student
        StudentProfile student = studentRepository.findByUserId(userId).orElse(null);
        if (student != null) {
            boolean hasBooking = clazz.getTutor().getBookings().stream()
                    .anyMatch(b -> b.getStudentId().equals(student.getId()) &&
                            (b.getStatus() == BookingStatus.CONFIRMED || b.getStatus() == BookingStatus.TRIAL));
            if (hasBooking) return Role.STUDENT;
        }

        throw new RuntimeException("Forbidden: Not a participant");
    }

    private boolean withinStartWindow(Session session) {
        long startMillis = session.getScheduledStartAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        long nowMillis = System.currentTimeMillis();
        long from = startMillis - (START_WINDOW_MINUTES_BEFORE * 60 * 1000);
        long to = startMillis + (START_WINDOW_MINUTES_AFTER * 60 * 1000);
        return nowMillis >= from && nowMillis <= to;
    }

    private boolean canComplete(Session session) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(session.getScheduledEndAt())) return false;
        return session.getStatus() != SessionStatus.CANCELLED && session.getStatus() != SessionStatus.MISSED;
    }

    private void maybeFlagDispute(Session session, LocalDateTime now) {
        if (session.getDisputeFlaggedAt() != null) return;

        // Check Start Dispute
        LocalDateTime firstStart = session.getTutorStartConfirmedAt() != null ? session.getTutorStartConfirmedAt() : session.getStudentStartConfirmedAt();
        if (firstStart != null) {
            long diffHours = Duration.between(firstStart, now).toHours();
            if (diffHours >= DISPUTE_HOURS) {
                session.setDisputeFlaggedAt(now);
                sendDisputeNotification(session);
                return;
            }
        }

        // Check Complete Dispute
        LocalDateTime firstComplete = session.getTutorCompleteConfirmedAt() != null ? session.getTutorCompleteConfirmedAt() : session.getStudentCompleteConfirmedAt();
        if (firstComplete != null) {
            long diffHours = Duration.between(firstComplete, now).toHours();
            if (diffHours >= DISPUTE_HOURS) {
                session.setDisputeFlaggedAt(now);
                sendDisputeNotification(session);
            }
        }
    }

    // ================= NOTIFICATIONS =================

    private void sendStartNotifications(Session session, String actorId, Role role, boolean tutorConf, boolean studentConf) {
        String tutorUserId = session.getClazz().getTutor().getUserId();
        List<String> studentUserIds = getStudentUserIds(session);

        // 1. Notify other party to confirm
        if (tutorConf && !studentConf && role == Role.TEACHER) {
            for (String uid : studentUserIds) {
                notificationService.createNotification(uid, "Xác nhận bắt đầu", "Gia sư đã xác nhận bắt đầu, vui lòng xác nhận.", "SESSION_WAIT_START", null);
            }
        }
        if (studentConf && !tutorConf && role == Role.STUDENT) {
            notificationService.createNotification(tutorUserId, "Xác nhận bắt đầu", "Học viên đã xác nhận bắt đầu, vui lòng xác nhận.", "SESSION_WAIT_START", null);
        }

        // 2. Notify Started
        if (session.getStatus() == SessionStatus.IN_PROGRESS) {
            // Notify Tutor
            notificationService.createNotification(tutorUserId, "Buổi học đã bắt đầu", "Buổi học đang diễn ra.", "SESSION_STARTED", null);
            // Notify Students
            for (String uid : studentUserIds) {
                notificationService.createNotification(uid, "Buổi học đã bắt đầu", "Buổi học đang diễn ra.", "SESSION_STARTED", null);
            }
        }
    }

    private void sendCompleteNotifications(Session session, String actorId, Role role, boolean tutorConf, boolean studentConf) {
        String tutorUserId = session.getClazz().getTutor().getUserId();
        List<String> studentUserIds = getStudentUserIds(session);

        // 1. Notify other party
        if (tutorConf && !studentConf && role == Role.TEACHER) {
            for (String uid : studentUserIds) {
                notificationService.createNotification(uid, "Xác nhận hoàn thành", "Gia sư đã xác nhận hoàn thành.", "SESSION_WAIT_COMPLETE", null);
            }
        }
        if (studentConf && !tutorConf && role == Role.STUDENT) {
            notificationService.createNotification(tutorUserId, "Xác nhận hoàn thành", "Học viên đã xác nhận hoàn thành.", "SESSION_WAIT_COMPLETE", null);
        }

        // 2. Notify Completed
        if (session.getStatus() == SessionStatus.COMPLETED) {
            notificationService.createNotification(tutorUserId, "Buổi học hoàn tất", "Buổi học đã kết thúc.", "SESSION_COMPLETED", null);
            for (String uid : studentUserIds) {
                notificationService.createNotification(uid, "Buổi học hoàn tất", "Buổi học đã kết thúc.", "SESSION_COMPLETED", null);
            }
        }
    }

    private void sendDisputeNotification(Session session) {
        String tutorUserId = session.getClazz().getTutor().getUserId();
        List<String> recipients = getStudentUserIds(session);
        recipients.add(tutorUserId);

        // Add Admins
        // List<String> adminIds = userRepository.findAllAdmins()...

        for (String uid : recipients) {
            notificationService.createNotification(uid, "Phiên học cần xem xét", "Buổi học bị đánh dấu tranh chấp.", "SESSION_DISPUTE", null);
        }
    }

    private List<String> getStudentUserIds(Session session) {
        return session.getClazz().getTutor().getBookings().stream()
                .map(b -> b.getStudent().getUser().getId())
                .collect(Collectors.toList());
    }

    private SessionResponse mapToResponse(Session s) {
        return SessionResponse.builder()
                .id(s.getId())
                .classId(s.getClassId())
                .scheduledStartAt(s.getScheduledStartAt())
                .scheduledEndAt(s.getScheduledEndAt())
                .status(s.getStatus())
                .startedAt(s.getStartedAt())
                .completedAt(s.getCompletedAt())
                .disputeFlaggedAt(s.getDisputeFlaggedAt())
                .tutorStartConfirmed(s.getTutorStartConfirmedAt() != null)
                .studentStartConfirmed(s.getStudentStartConfirmedAt() != null)
                .tutorCompleteConfirmed(s.getTutorCompleteConfirmedAt() != null)
                .studentCompleteConfirmed(s.getStudentCompleteConfirmedAt() != null)
                .build();
    }
}
