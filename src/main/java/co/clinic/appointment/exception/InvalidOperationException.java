package co.clinic.appointment.exception;

import co.clinic.appointment.response.ResponseCode;
import org.springframework.http.HttpStatus;

public class InvalidOperationException extends BaseException {

    public InvalidOperationException(String message) {
        super("Invalid Operation", message, ResponseCode.INVALID_OPERATION, HttpStatus.BAD_REQUEST);
    }
}
