package co.clinic.appointment.notification;

public class NotificationEvent {

    private final String appointmentId;
    private final String patientId;
    private final String practitionerId;
    private final NotificationEventType eventType;
    private final String message;

    private NotificationEvent(Builder b) {
        this.appointmentId = b.appointmentId;
        this.patientId = b.patientId;
        this.practitionerId = b.practitionerId;
        this.eventType = b.eventType;
        this.message = b.message;
    }

    public String getAppointmentId() { return appointmentId; }
    public String getPatientId() { return patientId; }
    public String getPractitionerId() { return practitionerId; }
    public NotificationEventType getEventType() { return eventType; }
    public String getMessage() { return message; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String appointmentId, patientId, practitionerId, message;
        private NotificationEventType eventType;

        public Builder appointmentId(String appointmentId) { this.appointmentId = appointmentId; return this; }
        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder practitionerId(String practitionerId) { this.practitionerId = practitionerId; return this; }
        public Builder eventType(NotificationEventType eventType) { this.eventType = eventType; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public NotificationEvent build() { return new NotificationEvent(this); }
    }
}
