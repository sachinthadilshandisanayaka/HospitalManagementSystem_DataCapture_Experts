package co.clinic.appointment.history;

import co.clinic.appointment.entity.AppointmentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "appointment_history")
public class AppointmentHistory {

    @Id
    private String id;

    @Indexed
    private String appointmentId;

    private AppointmentHistoryAction action;
    private AppointmentStatus statusAfter;
    private String notes;
    private String performedBy;
    private Instant performedAt;

    private AppointmentHistory(Builder b) {
        this.appointmentId = b.appointmentId; this.action = b.action;
        this.statusAfter = b.statusAfter; this.notes = b.notes;
        this.performedBy = b.performedBy; this.performedAt = b.performedAt;
    }

    public String getId() { return id; }
    public String getAppointmentId() { return appointmentId; }
    public AppointmentHistoryAction getAction() { return action; }
    public AppointmentStatus getStatusAfter() { return statusAfter; }
    public String getNotes() { return notes; }
    public String getPerformedBy() { return performedBy; }
    public Instant getPerformedAt() { return performedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String appointmentId, notes, performedBy;
        private AppointmentHistoryAction action;
        private AppointmentStatus statusAfter;
        private Instant performedAt;

        public Builder appointmentId(String appointmentId) { this.appointmentId = appointmentId; return this; }
        public Builder action(AppointmentHistoryAction action) { this.action = action; return this; }
        public Builder statusAfter(AppointmentStatus statusAfter) { this.statusAfter = statusAfter; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder performedBy(String performedBy) { this.performedBy = performedBy; return this; }
        public Builder performedAt(Instant performedAt) { this.performedAt = performedAt; return this; }
        public AppointmentHistory build() { return new AppointmentHistory(this); }
    }
}
