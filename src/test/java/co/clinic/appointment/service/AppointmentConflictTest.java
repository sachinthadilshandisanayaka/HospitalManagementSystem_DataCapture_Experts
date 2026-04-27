package co.clinic.appointment.service;

import co.clinic.appointment.audit.AuditService;
import co.clinic.appointment.dto.request.AppointmentRequest;
import co.clinic.appointment.dto.response.AppointmentResponse;
import co.clinic.appointment.entity.Appointment;
import co.clinic.appointment.entity.AppointmentStatus;
import co.clinic.appointment.entity.Patient;
import co.clinic.appointment.entity.Practitioner;
import co.clinic.appointment.exception.AppointmentConflictException;
import co.clinic.appointment.history.AppointmentHistoryService;
import co.clinic.appointment.mapper.AppointmentMapper;
import co.clinic.appointment.notification.NotificationStrategyFactory;
import co.clinic.appointment.repository.AppointmentRepository;
import co.clinic.appointment.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentConflictTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private PractitionerService practitionerService;
    @Mock private AppointmentMapper appointmentMapper;
    @Mock private AuditService auditService;
    @Mock private AppointmentHistoryService historyService;
    @Mock private NotificationStrategyFactory notificationFactory;

    private AppointmentService appointmentService;

    private static final String PRACTITIONER_ID = "pract-001";
    private static final String PATIENT_ID = "patient-001";

    private final Instant baseTime = Instant.parse("2026-05-01T09:00:00Z");

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                appointmentRepository, patientRepository, practitionerService,
                appointmentMapper, auditService, historyService, notificationFactory);

        Patient patient = new Patient();
        patient.setId(PATIENT_ID);
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(practitionerService.findById(PRACTITIONER_ID)).thenReturn(new Practitioner());
    }

    @Test
    @DisplayName("Should throw AppointmentConflictException when new appointment overlaps an existing BOOKED slot")
    void shouldThrowConflict_whenAppointmentsOverlap() {
        Appointment existing = bookedAppointment(baseTime, baseTime.plus(1, ChronoUnit.HOURS));

        AppointmentRequest request = buildRequest(
                baseTime.plus(30, ChronoUnit.MINUTES),
                baseTime.plus(90, ChronoUnit.MINUTES));

        when(appointmentRepository.findOverlapping(eq(PRACTITIONER_ID), any(), any()))
                .thenReturn(List.of(existing));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("overlapping");
    }

    @Test
    @DisplayName("Should throw AppointmentConflictException when new appointment is fully inside an existing BOOKED slot")
    void shouldThrowConflict_whenNewSlotIsInsideExisting() {
        Appointment existing = bookedAppointment(baseTime, baseTime.plus(2, ChronoUnit.HOURS));

        AppointmentRequest request = buildRequest(
                baseTime.plus(15, ChronoUnit.MINUTES),
                baseTime.plus(105, ChronoUnit.MINUTES));

        when(appointmentRepository.findOverlapping(eq(PRACTITIONER_ID), any(), any()))
                .thenReturn(List.of(existing));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(AppointmentConflictException.class);
    }

    @Test
    @DisplayName("Should succeed when new appointment does not overlap any existing BOOKED slot")
    void shouldSucceed_whenNoOverlap() {
        AppointmentRequest request = buildRequest(
                baseTime.plus(1, ChronoUnit.HOURS),
                baseTime.plus(2, ChronoUnit.HOURS));

        when(appointmentRepository.findOverlapping(eq(PRACTITIONER_ID), any(), any()))
                .thenReturn(List.of());

        Appointment saved = bookedAppointment(request.getStartTime(), request.getEndTime());
        saved.setId("new-appt-id");
        when(appointmentMapper.toEntity(request)).thenReturn(saved);
        when(appointmentRepository.save(any())).thenReturn(saved);

        AppointmentResponse response = AppointmentResponse.builder()
                .id("new-appt-id").status(AppointmentStatus.BOOKED).build();
        when(appointmentMapper.toResponse(saved)).thenReturn(response);

        assertThatNoException().isThrownBy(() -> appointmentService.create(request));
    }

    private AppointmentRequest buildRequest(Instant start, Instant end) {
        AppointmentRequest req = new AppointmentRequest();
        req.setPatientId(PATIENT_ID);
        req.setPractitionerId(PRACTITIONER_ID);
        req.setStartTime(start);
        req.setEndTime(end);
        return req;
    }

    private Appointment bookedAppointment(Instant start, Instant end) {
        Appointment appt = new Appointment();
        appt.setId("existing-001");
        appt.setPractitionerId(PRACTITIONER_ID);
        appt.setPatientId(PATIENT_ID);
        appt.setStartTime(start);
        appt.setEndTime(end);
        appt.setStatus(AppointmentStatus.BOOKED);
        return appt;
    }
}
