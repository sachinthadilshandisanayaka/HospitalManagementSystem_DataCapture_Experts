package co.clinic.appointment.dto.response;

import java.time.Instant;

public class PractitionerResponse {

    private String id;
    private String fullName;
    private String registrationNo;
    private String specialty;
    private String personType;
    private Instant createdAt;
    private Instant updatedAt;

    private PractitionerResponse(Builder b) {
        this.id = b.id; this.fullName = b.fullName; this.registrationNo = b.registrationNo;
        this.specialty = b.specialty; this.personType = b.personType;
        this.createdAt = b.createdAt; this.updatedAt = b.updatedAt;
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getRegistrationNo() { return registrationNo; }
    public String getSpecialty() { return specialty; }
    public String getPersonType() { return personType; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id, fullName, registrationNo, specialty, personType;
        private Instant createdAt, updatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder registrationNo(String registrationNo) { this.registrationNo = registrationNo; return this; }
        public Builder specialty(String specialty) { this.specialty = specialty; return this; }
        public Builder personType(String personType) { this.personType = personType; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public PractitionerResponse build() { return new PractitionerResponse(this); }
    }
}
