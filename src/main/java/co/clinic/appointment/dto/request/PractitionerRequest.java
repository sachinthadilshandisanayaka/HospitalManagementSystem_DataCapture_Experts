package co.clinic.appointment.dto.request;

import jakarta.validation.constraints.NotBlank;

public class PractitionerRequest {

    @NotBlank(message = "fullName is required")
    private String fullName;

    @NotBlank(message = "registrationNo is required")
    private String registrationNo;

    @NotBlank(message = "specialty is required")
    private String specialty;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}
