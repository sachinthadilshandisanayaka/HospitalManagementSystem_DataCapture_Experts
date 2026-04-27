package co.clinic.appointment.entity;

public abstract class Person extends BaseEntity {

    private String fullName;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public abstract String getPersonType();
}
