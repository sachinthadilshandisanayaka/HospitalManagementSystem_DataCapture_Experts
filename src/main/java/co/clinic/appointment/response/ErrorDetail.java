package co.clinic.appointment.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDetail {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final List<String> details;
    private final String path;

    private ErrorDetail(Builder builder) {
        this.timestamp = builder.timestamp;
        this.status = builder.status;
        this.error = builder.error;
        this.message = builder.message;
        this.details = builder.details;
        this.path = builder.path;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<String> getDetails() { return details; }
    public String getPath() { return path; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private List<String> details;
        private String path;

        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder status(int status) { this.status = status; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder details(List<String> details) { this.details = details; return this; }
        public Builder path(String path) { this.path = path; return this; }
        public ErrorDetail build() { return new ErrorDetail(this); }
    }
}
