package co.clinic.appointment.exception;

import co.clinic.appointment.util.ResponseCode;
import org.springframework.http.HttpStatus;

public class DuplicateRecordException extends BaseException {

    public DuplicateRecordException(String message) {
        super("Duplicate Record", message, ResponseCode.DUPLICATE_DATA, HttpStatus.CONFLICT);
    }
}
