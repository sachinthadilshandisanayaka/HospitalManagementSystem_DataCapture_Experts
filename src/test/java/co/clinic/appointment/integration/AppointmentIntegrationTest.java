package co.clinic.appointment.integration;

import co.clinic.appointment.audit.AuditService;
import co.clinic.appointment.dto.request.AppointmentRequest;
import co.clinic.appointment.dto.response.AppointmentResponse;
import co.clinic.appointment.util.AppointmentStatus;
import co.clinic.appointment.exception.AppointmentConflictException;
import co.clinic.appointment.exception.DataNotFoundException;
import co.clinic.appointment.history.AppointmentHistoryService;
import co.clinic.appointment.mapper.AppointmentMapper;
import co.clinic.appointment.notification.NotificationStrategyFactory;
import co.clinic.appointment.entity.Appointment;
import co.clinic.appointment.entity.Patient;
import co.clinic.appointment.entity.Practitioner;
import co.clinic.appointment.repository.AppointmentRepository;
import co.clinic.appointment.repository.PatientRepository;
import co.clinic.appointment.service.AppointmentService;
import co.clinic.appointment.service.PractitionerService;
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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Service-layer integration test for appointment creation flows.
 * Tests the full service logic including conflict detection, notifications, and audit.
 * (Controller-level HTTP integration tests require a running MongoDB — see README.)
 */
@ExtendWith(MockitoExtension.class)
class AppointmentIntegrationTest {

    @Mock private AppointmentRepository appointmentRepository;
    @Mock private PatientRepository patientRepository;
    @Mock private PractitionerService practitionerService;
    @Mock private AppointmentMapper appointmentMapper;
    @Mock private AuditService auditService;
    @Mock private AppointmentHistoryService historyService;
    @Mock private NotificationStrategyFactory notificationFactory;

    private AppointmentService appointmentService;

    private static final String PATIENT_ID = "patient-integration-001";
    private static final String PRACTITIONER_ID = "pract-integration-001";

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService(
                appointmentRepository, patientRepository, practitionerService,
                appointmentMapper, auditService, historyService, notificationFactory);
    }

    @Test
    @DisplayName("Happy path: appointment creation succeeds when no overlap and entities exist")
    void shouldCreateAppointment_happyPath() {
        Instant start = Instant.now().plus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.HOURS);
        Instant end = start.plus(30, ChronoUnit.MINUTES);

        Patient patient = new Patient();
        patient.setId(PATIENT_ID);
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(practitionerService.findById(PRACTITIONER_ID)).thenReturn(new Practitioner());
        when(appointmentRepository.findOverlapping(eq(PRACTITIONER_ID), any(), any())).thenReturn(List.of());

        Appointment saved = new Appointment();
        saved.setId("appt-001");
        saved.setPatientId(PATIENT_ID);
        saved.setPractitionerId(PRACTITIONER_ID);
        saved.setStartTime(start);
        saved.setEndTime(end);
        saved.setStatus(AppointmentStatus.BOOKED);

        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(PATIENT_ID);
        request.setPractitionerId(PRACTITIONER_ID);
        request.setStartTime(start);
        request.setEndTime(end);

        when(appointmentMapper.toEntity(request)).thenReturn(saved);
        when(appointmentRepository.save(any())).thenReturn(saved);

        AppointmentResponse response = AppointmentResponse.builder()
                .id("appt-001").status(AppointmentStatus.BOOKED).build();
        when(appointmentMapper.toResponse(saved)).thenReturn(response);

        AppointmentResponse result = appointmentService.create(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("appt-001");
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.BOOKED);
    }

    @Test
    @DisplayName("Should throw AppointmentConflictException when slot overlaps")
    void shouldThrowConflict_onOverlappingSlot() {
        Instant start = Instant.now().plus(2, ChronoUnit.DAYS).truncatedTo(ChronoUnit.HOURS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        Patient patient = new Patient();
        patient.setId(PATIENT_ID);
        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.of(patient));
        when(practitionerService.findById(PRACTITIONER_ID)).thenReturn(new Practitioner());

        Appointment existing = new Appointment();
        existing.setStatus(AppointmentStatus.BOOKED);
        when(appointmentRepository.findOverlapping(eq(PRACTITIONER_ID), any(), any()))
                .thenReturn(List.of(existing));

        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(PATIENT_ID);
        request.setPractitionerId(PRACTITIONER_ID);
        request.setStartTime(start.plus(30, ChronoUnit.MINUTES));
        request.setEndTime(end.plus(30, ChronoUnit.MINUTES));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(AppointmentConflictException.class)
                .hasMessageContaining("overlapping");
    }

    @Test
    @DisplayName("Should throw DataNotFoundException when patient does not exist")
    void shouldThrowNotFound_whenPatientMissing() {
        Instant start = Instant.now().plus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.HOURS);

        when(patientRepository.findById(PATIENT_ID)).thenReturn(Optional.empty());

        AppointmentRequest request = new AppointmentRequest();
        request.setPatientId(PATIENT_ID);
        request.setPractitionerId(PRACTITIONER_ID);
        request.setStartTime(start);
        request.setEndTime(start.plus(30, ChronoUnit.MINUTES));

        assertThatThrownBy(() -> appointmentService.create(request))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("Patient not found");
    }
}
