package co.clinic.appointment.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateNotesRequest {

    @NotBlank(message = "notes is required")
    private String notes;

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
