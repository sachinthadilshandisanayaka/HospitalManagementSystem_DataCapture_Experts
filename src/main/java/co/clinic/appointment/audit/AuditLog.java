package co.clinic.appointment.audit;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "audit_logs")
public class AuditLog {

    @Id
    private String id;
    private String entityId;
    private String entityType;
    private String action;
    private Object oldValue;
    private Object newValue;
    private String changedBy;
    private Instant changedAt;
    private String requestId;

    private AuditLog(Builder b) {
        this.entityId = b.entityId; this.entityType = b.entityType; this.action = b.action;
        this.oldValue = b.oldValue; this.newValue = b.newValue; this.changedBy = b.changedBy;
        this.changedAt = b.changedAt; this.requestId = b.requestId;
    }

    public String getId() { return id; }
    public String getEntityId() { return entityId; }
    public String getEntityType() { return entityType; }
    public String getAction() { return action; }
    public Object getOldValue() { return oldValue; }
    public Object getNewValue() { return newValue; }
    public String getChangedBy() { return changedBy; }
    public Instant getChangedAt() { return changedAt; }
    public String getRequestId() { return requestId; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String entityId, entityType, action, changedBy, requestId;
        private Object oldValue, newValue;
        private Instant changedAt;

        public Builder entityId(String entityId) { this.entityId = entityId; return this; }
        public Builder entityType(String entityType) { this.entityType = entityType; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder oldValue(Object oldValue) { this.oldValue = oldValue; return this; }
        public Builder newValue(Object newValue) { this.newValue = newValue; return this; }
        public Builder changedBy(String changedBy) { this.changedBy = changedBy; return this; }
        public Builder changedAt(Instant changedAt) { this.changedAt = changedAt; return this; }
        public Builder requestId(String requestId) { this.requestId = requestId; return this; }
        public AuditLog build() { return new AuditLog(this); }
    }
}
