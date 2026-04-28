package co.clinic.appointment.service;

import co.clinic.appointment.audit.AuditService;
import co.clinic.appointment.dto.request.AppointmentRequest;
import co.clinic.appointment.dto.request.UpdateNotesRequest;
import co.clinic.appointment.dto.response.AppointmentResponse;
import co.clinic.appointment.dto.response.PageResponse;
import co.clinic.appointment.entity.Appointment;
import co.clinic.appointment.util.AppointmentStatus;
import co.clinic.appointment.exception.AppointmentConflictException;
import co.clinic.appointment.exception.DataNotFoundException;
import co.clinic.appointment.exception.InvalidDataException;
import co.clinic.appointment.exception.InvalidOperationException;
import co.clinic.appointment.util.AppointmentHistoryAction;
import co.clinic.appointment.history.AppointmentHistoryService;
import co.clinic.appointment.mapper.AppointmentMapper;
import co.clinic.appointment.notification.NotificationEvent;
import co.clinic.appointment.util.NotificationEventType;
import co.clinic.appointment.notification.NotificationStrategyFactory;
import co.clinic.appointment.repository.AppointmentRepository;
import co.clinic.appointment.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PractitionerService practitionerService;
    private final AppointmentMapper appointmentMapper;
    private final AuditService auditService;
    private final AppointmentHistoryService historyService;
    private final NotificationStrategyFactory notificationFactory;

    public AppointmentService(AppointmentRepository appointmentRepository,
                               PatientRepository patientRepository,
                               PractitionerService practitionerService,
                               AppointmentMapper appointmentMapper,
                               AuditService auditService,
                               AppointmentHistoryService historyService,
                               NotificationStrategyFactory notificationFactory) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.practitionerService = practitionerService;
        this.appointmentMapper = appointmentMapper;
        this.auditService = auditService;
        this.historyService = historyService;
        this.notificationFactory = notificationFactory;
    }

    public AppointmentResponse create(AppointmentRequest request) {
        validateTimings(request.getStartTime(), request.getEndTime());

        patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new DataNotFoundException("Patient not found with id: " + request.getPatientId()));
        practitionerService.findById(request.getPractitionerId());

        List<Appointment> overlaps = appointmentRepository.findOverlapping(
                request.getPractitionerId(), request.getStartTime(), request.getEndTime());

        if (!overlaps.isEmpty()) {
            throw new AppointmentConflictException(
                    "Practitioner already has a BOOKED appointment overlapping the requested time slot");
        }

        Appointment appointment = appointmentMapper.toEntity(request);
        Appointment saved = appointmentRepository.save(appointment);

        historyService.record(saved, AppointmentHistoryAction.CREATED, "system");
        auditService.log(saved.getId(), "APPOINTMENT", "CREATED", null, saved, "system");

        notificationFactory.notifyAll(NotificationEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .practitionerId(saved.getPractitionerId())
                .eventType(NotificationEventType.APPOINTMENT_BOOKED)
                .message("Appointment booked for " + saved.getStartTime())
                .build());

        log.info("Appointment created: id={}", saved.getId());
        return appointmentMapper.toResponse(saved);
    }

    public AppointmentResponse getById(String id) {
        return appointmentMapper.toResponse(findById(id));
    }

    public PageResponse<AppointmentResponse> list(String practitionerId, LocalDate date,
                                                   AppointmentStatus status, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        Instant dayStart = date != null ? date.atStartOfDay(ZoneOffset.UTC).toInstant() : null;
        Instant dayEnd   = date != null ? date.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant() : null;

        Page<Appointment> result;

        if (practitionerId != null && !practitionerId.isBlank() && date != null) {
            result = appointmentRepository.findByPractitionerIdAndDateRange(practitionerId, dayStart, dayEnd, pageable);

        } else if (practitionerId != null && !practitionerId.isBlank() && status != null) {
            result = appointmentRepository.findByPractitionerIdAndStatus(practitionerId, status, pageable);

        } else if (practitionerId != null && !practitionerId.isBlank()) {
            result = appointmentRepository.findByPractitionerId(practitionerId, pageable);

        } else if (date != null) {
            result = appointmentRepository.findByDateRange(dayStart, dayEnd, pageable);

        } else if (status != null) {
            result = appointmentRepository.findByStatus(status, pageable);

        } else {
            result = appointmentRepository.findAll(pageable);
        }

        return PageResponse.of(result.map(appointmentMapper::toResponse));
    }

    public AppointmentResponse cancel(String id) {
        Appointment appointment = findById(id);

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new InvalidOperationException(
                    "Only BOOKED appointments can be cancelled. Current status: " + appointment.getStatus());
        }

        Appointment before = snapshotOf(appointment);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);

        historyService.record(saved, AppointmentHistoryAction.CANCELLED, "system");
        auditService.log(saved.getId(), "APPOINTMENT", "CANCELLED", before, saved, "system");

        notificationFactory.notifyAll(NotificationEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .practitionerId(saved.getPractitionerId())
                .eventType(NotificationEventType.APPOINTMENT_CANCELLED)
                .message("Appointment cancelled")
                .build());

        log.info("Appointment cancelled: id={}", saved.getId());
        return appointmentMapper.toResponse(saved);
    }

    public AppointmentResponse complete(String id) {
        Appointment appointment = findById(id);

        if (appointment.getStatus() != AppointmentStatus.BOOKED) {
            throw new InvalidOperationException(
                    "Only BOOKED appointments can be marked as completed. Current status: " + appointment.getStatus());
        }

        Appointment before = snapshotOf(appointment);
        appointment.setStatus(AppointmentStatus.COMPLETED);
        Appointment saved = appointmentRepository.save(appointment);

        historyService.record(saved, AppointmentHistoryAction.COMPLETED, "system");
        auditService.log(saved.getId(), "APPOINTMENT", "COMPLETED", before, saved, "system");

        notificationFactory.notifyAll(NotificationEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .practitionerId(saved.getPractitionerId())
                .eventType(NotificationEventType.APPOINTMENT_COMPLETED)
                .message("Appointment completed")
                .build());

        log.info("Appointment completed: id={}", saved.getId());
        return appointmentMapper.toResponse(saved);
    }

    public AppointmentResponse updateNotes(String id, UpdateNotesRequest request) {
        Appointment appointment = findById(id);

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidOperationException("Completed appointments cannot be edited");
        }

        Appointment before = snapshotOf(appointment);
        appointment.setNotes(request.getNotes());
        Appointment saved = appointmentRepository.save(appointment);

        historyService.record(saved, AppointmentHistoryAction.UPDATED, "system");
        auditService.log(saved.getId(), "APPOINTMENT", "NOTES_UPDATED", before, saved, "system");

        notificationFactory.notifyAll(NotificationEvent.builder()
                .appointmentId(saved.getId())
                .patientId(saved.getPatientId())
                .practitionerId(saved.getPractitionerId())
                .eventType(NotificationEventType.APPOINTMENT_UPDATED)
                .message("Appointment notes updated")
                .build());

        log.info("Appointment notes updated: id={}", saved.getId());
        return appointmentMapper.toResponse(saved);
    }

    public Appointment findById(String id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Appointment not found with id: " + id));
    }

    private void validateTimings(Instant start, Instant end) {
        if (!end.isAfter(start)) {
            throw new InvalidDataException("endTime must be after startTime");
        }
    }

    private Appointment snapshotOf(Appointment src) {
        Appointment snap = new Appointment();
        snap.setId(src.getId());
        snap.setPatientId(src.getPatientId());
        snap.setPractitionerId(src.getPractitionerId());
        snap.setStartTime(src.getStartTime());
        snap.setEndTime(src.getEndTime());
        snap.setStatus(src.getStatus());
        snap.setNotes(src.getNotes());
        return snap;
    }
}
