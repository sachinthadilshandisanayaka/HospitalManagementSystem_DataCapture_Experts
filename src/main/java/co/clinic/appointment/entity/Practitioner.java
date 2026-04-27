package co.clinic.appointment.entity;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "practitioners")
public class Practitioner extends Person {

    @Indexed(unique = true)
    private String registrationNo;

    private String specialty;

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    @Override
    public String getPersonType() { return "PRACTITIONER"; }
}
