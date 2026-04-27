package co.clinic.appointment.exception;

import co.clinic.appointment.response.ResponseCode;
import org.springframework.http.HttpStatus;

public class DataNotFoundException extends BaseException {

    public DataNotFoundException(String message) {
        super("Not Found", message, ResponseCode.MISSING_DATA, HttpStatus.NOT_FOUND);
    }
}
