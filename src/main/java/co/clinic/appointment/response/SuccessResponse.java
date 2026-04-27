package co.clinic.appointment.response;

public class SuccessResponse {

    private final Object data;
    private final String message;
    private final boolean success;
    private final int code;

    private SuccessResponse(Builder builder) {
        this.data = builder.data;
        this.message = builder.message;
        this.success = builder.success;
        this.code = builder.code;
    }

    public Object getData() { return data; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    public int getCode() { return code; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Object data;
        private String message;
        private boolean success;
        private int code;

        public Builder data(Object data) { this.data = data; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder success(boolean success) { this.success = success; return this; }
        public Builder code(int code) { this.code = code; return this; }
        public SuccessResponse build() { return new SuccessResponse(this); }
    }
}
