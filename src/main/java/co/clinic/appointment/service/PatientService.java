package co.clinic.appointment.service;

import co.clinic.appointment.audit.AuditService;
import co.clinic.appointment.dto.request.PatientRequest;
import co.clinic.appointment.dto.response.PageResponse;
import co.clinic.appointment.dto.response.PatientResponse;
import co.clinic.appointment.entity.Patient;
import co.clinic.appointment.exception.DataNotFoundException;
import co.clinic.appointment.exception.DuplicateRecordException;
import co.clinic.appointment.mapper.PatientMapper;
import co.clinic.appointment.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private static final Logger log = LoggerFactory.getLogger(PatientService.class);

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private final AuditService auditService;

    public PatientService(PatientRepository patientRepository,
                          PatientMapper patientMapper,
                          AuditService auditService) {
        this.patientRepository = patientRepository;
        this.patientMapper = patientMapper;
        this.auditService = auditService;
    }

    public PatientResponse create(PatientRequest request) {
        if (patientRepository.existsByNationalId(request.getNationalId())) {
            throw new DuplicateRecordException(
                    "Patient with nationalId '" + request.getNationalId() + "' already exists");
        }
        Patient patient = patientMapper.toEntity(request);
        Patient saved = patientRepository.save(patient);
        auditService.log(saved.getId(), "PATIENT", "CREATED", null, saved, "system");
        log.info("Patient created: id={}", saved.getId());
        return patientMapper.toResponse(saved);
    }

    public PatientResponse getById(String id) {
        return patientMapper.toResponse(findById(id));
    }

    public PageResponse<PatientResponse> search(String nationalId, String name, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        if (nationalId != null && !nationalId.isBlank()) {
            return PageResponse.of(
                    patientRepository.findByNationalIdContainingIgnoreCase(nationalId, pageable)
                            .map(patientMapper::toResponse));
        }
        if (name != null && !name.isBlank()) {
            return PageResponse.of(
                    patientRepository.findByFullNameContainingIgnoreCase(name, pageable)
                            .map(patientMapper::toResponse));
        }
        // No filter — return all patients paginated
        return PageResponse.of(
                patientRepository.findAll(pageable)
                        .map(patientMapper::toResponse));
    }

    private Patient findById(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Patient not found with id: " + id));
    }
}
