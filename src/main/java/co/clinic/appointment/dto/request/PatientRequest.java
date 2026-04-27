package co.clinic.appointment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class PatientRequest {

    @NotBlank(message = "fullName is required")
    @Size(min = 3, message = "fullName must be at least 3 characters")
    private String fullName;

    @NotBlank(message = "nationalId is required")
    private String nationalId;

    @NotNull(message = "dateOfBirth is required")
    private LocalDate dateOfBirth;

    @Email(message = "email must be a valid email address")
    private String email;

    private String phone;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
