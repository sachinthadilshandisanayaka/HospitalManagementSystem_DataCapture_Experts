package co.clinic.appointment.service;

import co.clinic.appointment.audit.AuditService;
import co.clinic.appointment.dto.request.PractitionerRequest;
import co.clinic.appointment.dto.response.PageResponse;
import co.clinic.appointment.dto.response.PractitionerResponse;
import co.clinic.appointment.entity.Practitioner;
import co.clinic.appointment.exception.DataNotFoundException;
import co.clinic.appointment.exception.DuplicateRecordException;
import co.clinic.appointment.mapper.PractitionerMapper;
import co.clinic.appointment.repository.PractitionerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class PractitionerService {

    private static final Logger log = LoggerFactory.getLogger(PractitionerService.class);

    private final PractitionerRepository practitionerRepository;
    private final PractitionerMapper practitionerMapper;
    private final AuditService auditService;

    public PractitionerService(PractitionerRepository practitionerRepository,
                                PractitionerMapper practitionerMapper,
                                AuditService auditService) {
        this.practitionerRepository = practitionerRepository;
        this.practitionerMapper = practitionerMapper;
        this.auditService = auditService;
    }

    public PractitionerResponse create(PractitionerRequest request) {
        if (practitionerRepository.existsByRegistrationNo(request.getRegistrationNo())) {
            throw new DuplicateRecordException(
                    "Practitioner with registrationNo '" + request.getRegistrationNo() + "' already exists");
        }
        Practitioner practitioner = practitionerMapper.toEntity(request);
        Practitioner saved = practitionerRepository.save(practitioner);
        auditService.log(saved.getId(), "PRACTITIONER", "CREATED", null, saved, "system");
        log.info("Practitioner created: id={} registrationNo={}", saved.getId(), saved.getRegistrationNo());
        return practitionerMapper.toResponse(saved);
    }

    public PractitionerResponse getById(String id) {
        return practitionerMapper.toResponse(findById(id));
    }

    public PageResponse<PractitionerResponse> getAll(String specialty, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);

        if (specialty != null && !specialty.isBlank()) {
            return PageResponse.of(
                    practitionerRepository.findBySpecialtyIgnoreCase(specialty, pageable)
                            .map(practitionerMapper::toResponse));
        }
        return PageResponse.of(
                practitionerRepository.findAll(pageable)
                        .map(practitionerMapper::toResponse));
    }

    public Practitioner findById(String id) {
        return practitionerRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Practitioner not found with id: " + id));
    }
}
