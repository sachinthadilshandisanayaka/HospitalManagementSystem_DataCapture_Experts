package co.clinic.appointment.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String entityId, String entityType, String action,
                    Object oldValue, Object newValue, String changedBy) {
        AuditLog auditLog = AuditLog.builder()
                .entityId(entityId)
                .entityType(entityType)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedBy(changedBy)
                .changedAt(Instant.now())
                .requestId(UUID.randomUUID().toString())
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit: entity={} id={} action={}", entityType, entityId, action);
    }
}
