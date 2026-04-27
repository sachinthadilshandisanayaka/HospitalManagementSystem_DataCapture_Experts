package co.clinic.appointment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class AppointmentRequest {

    @NotBlank(message = "patientId is required")
    private String patientId;

    @NotBlank(message = "practitionerId is required")
    private String practitionerId;

    @NotNull(message = "startTime is required (ISO-8601)")
    private Instant startTime;

    @NotNull(message = "endTime is required (ISO-8601)")
    private Instant endTime;

    private String notes;

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPractitionerId() { return practitionerId; }
    public void setPractitionerId(String practitionerId) { this.practitionerId = practitionerId; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
