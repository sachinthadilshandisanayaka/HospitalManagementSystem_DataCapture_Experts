package co.clinic.appointment.mapper;

import co.clinic.appointment.dto.request.PatientRequest;
import co.clinic.appointment.dto.response.PatientResponse;
import co.clinic.appointment.entity.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequest request) {
        Patient patient = new Patient();
        patient.setFullName(request.getFullName());
        patient.setNationalId(request.getNationalId());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setEmail(request.getEmail());
        patient.setPhone(request.getPhone());
        return patient;
    }

    public PatientResponse toResponse(Patient patient) {
        return PatientResponse.builder()
                .id(patient.getId())
                .fullName(patient.getFullName())
                .nationalId(patient.getNationalId())
                .dateOfBirth(patient.getDateOfBirth())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .personType(patient.getPersonType())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}
