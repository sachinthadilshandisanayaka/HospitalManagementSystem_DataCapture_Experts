package co.clinic.appointment.util;

public final class ResponseCode {

    public static final int SUCCESS           = 1000;
    public static final int INVALID_INPUT     = 1001;
    public static final int MISSING_DATA      = 1002;
    public static final int ERROR_OPERATION   = 1003;
    public static final int INVALID_OPERATION = 1004;
    public static final int CONFLICT_DATA     = 1005;
    public static final int DUPLICATE_DATA    = 1006;

    private ResponseCode() {}
}
