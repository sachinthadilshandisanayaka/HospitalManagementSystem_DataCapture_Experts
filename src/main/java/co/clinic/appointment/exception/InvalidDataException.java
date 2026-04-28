package co.clinic.appointment.exception;

import co.clinic.appointment.util.ResponseCode;
import org.springframework.http.HttpStatus;

public class InvalidDataException extends BaseException {

    public InvalidDataException(String message) {
        super("Invalid Data", message, ResponseCode.INVALID_INPUT, HttpStatus.BAD_REQUEST);
    }

    public InvalidDataException(String message, Object details) {
        super("Invalid Data", message, ResponseCode.INVALID_INPUT, HttpStatus.BAD_REQUEST, details);
    }
}
