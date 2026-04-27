package co.clinic.appointment.exception;

import org.springframework.http.HttpStatus;

public abstract class BaseException extends RuntimeException {

    private final String error;
    private final Integer code;
    private final HttpStatus httpStatus;
    private final Object details;

    protected BaseException(String error, String message, Integer code, HttpStatus httpStatus) {
        super(message);
        this.error = error;
        this.code = code;
        this.httpStatus = httpStatus;
        this.details = null;
    }

    protected BaseException(String error, String message, Integer code, HttpStatus httpStatus, Object details) {
        super(message);
        this.error = error;
        this.code = code;
        this.httpStatus = httpStatus;
        this.details = details;
    }

    public String getError() { return error; }
    public Integer getCode() { return code; }
    public HttpStatus getHttpStatus() { return httpStatus; }
    public Object getDetails() { return details; }
}
