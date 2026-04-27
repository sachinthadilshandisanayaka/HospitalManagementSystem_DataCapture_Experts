package co.clinic.appointment.dto.response;

import co.clinic.appointment.entity.AppointmentStatus;

import java.time.Instant;

public class AppointmentResponse {

    private String id;
    private String patientId;
    private String practitionerId;
    private Instant startTime;
    private Instant endTime;
    private AppointmentStatus status;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;
    private Long version;

    private AppointmentResponse(Builder b) {
        this.id = b.id; this.patientId = b.patientId; this.practitionerId = b.practitionerId;
        this.startTime = b.startTime; this.endTime = b.endTime; this.status = b.status;
        this.notes = b.notes; this.createdAt = b.createdAt; this.updatedAt = b.updatedAt;
        this.version = b.version;
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getPractitionerId() { return practitionerId; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public AppointmentStatus getStatus() { return status; }
    public String getNotes() { return notes; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Long getVersion() { return version; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, patientId, practitionerId, notes;
        private Instant startTime, endTime, createdAt, updatedAt;
        private AppointmentStatus status;
        private Long version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder patientId(String patientId) { this.patientId = patientId; return this; }
        public Builder practitionerId(String practitionerId) { this.practitionerId = practitionerId; return this; }
        public Builder startTime(Instant startTime) { this.startTime = startTime; return this; }
        public Builder endTime(Instant endTime) { this.endTime = endTime; return this; }
        public Builder status(AppointmentStatus status) { this.status = status; return this; }
        public Builder notes(String notes) { this.notes = notes; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Long version) { this.version = version; return this; }
        public AppointmentResponse build() { return new AppointmentResponse(this); }
    }
}
