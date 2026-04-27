package co.clinic.appointment.mapper;

import co.clinic.appointment.dto.request.PractitionerRequest;
import co.clinic.appointment.dto.response.PractitionerResponse;
import co.clinic.appointment.entity.Practitioner;
import org.springframework.stereotype.Component;

@Component
public class PractitionerMapper {

    public Practitioner toEntity(PractitionerRequest request) {
        Practitioner practitioner = new Practitioner();
        practitioner.setFullName(request.getFullName());
        practitioner.setRegistrationNo(request.getRegistrationNo());
        practitioner.setSpecialty(request.getSpecialty());
        return practitioner;
    }

    public PractitionerResponse toResponse(Practitioner practitioner) {
        return PractitionerResponse.builder()
                .id(practitioner.getId())
                .fullName(practitioner.getFullName())
                .registrationNo(practitioner.getRegistrationNo())
                .specialty(practitioner.getSpecialty())
                .personType(practitioner.getPersonType())
                .createdAt(practitioner.getCreatedAt())
                .updatedAt(practitioner.getUpdatedAt())
                .build();
    }
}
