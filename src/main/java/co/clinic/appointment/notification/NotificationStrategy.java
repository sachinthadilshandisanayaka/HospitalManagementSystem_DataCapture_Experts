package co.clinic.appointment.notification;

public interface NotificationStrategy {

    void send(NotificationEvent event);

    String getChannel();
}
