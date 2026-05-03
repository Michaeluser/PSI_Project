package sk.stuba.fiit.bikeflow.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Cross-cutting notification infrastructure from the F4 Common package.
 * In this local implementation notifications are delivered as log entries.
 * In production this would delegate to an email/SMS gateway.
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void sendConfirmation(Notifiable notifiable, String message) {
        log.info("[NOTIFICATION] Confirmation → {} (ref: {}) | {}",
                notifiable.getEmail(),
                notifiable.getPhone(),
                message);
    }

    public void sendStatusUpdate(Notifiable notifiable, String message) {
        log.info("[NOTIFICATION] Status update → {} (ref: {}) | {}",
                notifiable.getEmail(),
                notifiable.getPhone(),
                message);
    }
}
