package co.clinic.appointment.mapper;

import co.clinic.appointment.dto.request.AppointmentRequest;
import co.clinic.appointment.dto.response.AppointmentResponse;
import co.clinic.appointment.entity.Appointment;
import co.clinic.appointment.entity.AppointmentStatus;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setPatientId(request.getPatientId());
        appointment.setPractitionerId(request.getPractitionerId());
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setStatus(AppointmentStatus.BOOKED);
        appointment.setNotes(request.getNotes());
        return appointment;
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .patientId(appointment.getPatientId())
                .practitionerId(appointment.getPractitionerId())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .version(appointment.getVersion())
                .build();
    }
}
