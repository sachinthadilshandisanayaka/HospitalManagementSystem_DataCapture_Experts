package co.clinic.appointment.exception;

import co.clinic.appointment.response.ResponseCode;
import org.springframework.http.HttpStatus;

public class AppointmentConflictException extends BaseException {

    public AppointmentConflictException(String message) {
        super("Appointment Conflict", message, ResponseCode.CONFLICT_DATA, HttpStatus.CONFLICT);
    }
}
