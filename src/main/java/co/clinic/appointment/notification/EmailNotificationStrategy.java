package co.clinic.appointment.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationStrategy implements NotificationStrategy {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationStrategy.class);

    @Override
    public void send(NotificationEvent event) {
        log.info("[EMAIL] Event: {} | AppointmentId: {} | PatientId: {} | Message: {}",
                event.getEventType(), event.getAppointmentId(), event.getPatientId(), event.getMessage());
    }

    @Override
    public String getChannel() { return "EMAIL"; }
}
