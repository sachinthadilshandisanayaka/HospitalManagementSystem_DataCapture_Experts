package co.clinic.appointment.dto.response;

import java.time.Instant;
import java.time.LocalDate;

public class PatientResponse {

    private String id;
    private String fullName;
    private String nationalId;
    private LocalDate dateOfBirth;
    private String email;
    private String phone;
    private String personType;
    private Instant createdAt;
    private Instant updatedAt;

    private PatientResponse(Builder b) {
        this.id = b.id; this.fullName = b.fullName; this.nationalId = b.nationalId;
        this.dateOfBirth = b.dateOfBirth; this.email = b.email; this.phone = b.phone;
        this.personType = b.personType; this.createdAt = b.createdAt; this.updatedAt = b.updatedAt;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getNationalId() { return nationalId; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPersonType() { return personType; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, fullName, nationalId, email, phone, personType;
        private LocalDate dateOfBirth;
        private Instant createdAt, updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder nationalId(String nationalId) { this.nationalId = nationalId; return this; }
        public Builder dateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder personType(String personType) { this.personType = personType; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public PatientResponse build() { return new PatientResponse(this); }
    }
}
