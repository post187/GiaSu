package Notification.Repository;

import Notification.Entity.ReminderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReminderLogRepository extends JpaRepository<ReminderLog, String> {
}
