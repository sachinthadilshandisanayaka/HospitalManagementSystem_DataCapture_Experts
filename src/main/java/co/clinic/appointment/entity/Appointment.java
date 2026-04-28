package co.clinic.appointment.entity;

import co.clinic.appointment.util.AppointmentStatus;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "appointments")
@CompoundIndexes({
        @CompoundIndex(name = "practitioner_start_idx", def = "{'practitionerId': 1, 'startTime': 1}"),
        @CompoundIndex(name = "practitioner_status_idx", def = "{'practitionerId': 1, 'status': 1}")
})
public class Appointment extends BaseEntity {

    private String patientId;
    private String practitionerId;
    private Instant startTime;
    private Instant endTime;

    @Indexed
    private AppointmentStatus status;

    private String notes;

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getPractitionerId() { return practitionerId; }
    public void setPractitionerId(String practitionerId) { this.practitionerId = practitionerId; }

    public Instant getStartTime() { return startTime; }
    public void setStartTime(Instant startTime) { this.startTime = startTime; }

    public Instant getEndTime() { return endTime; }
    public void setEndTime(Instant endTime) { this.endTime = endTime; }

    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
