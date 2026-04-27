package co.clinic.appointment.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class SuccessResponseHandler {

    private SuccessResponseHandler() {}

    public static ResponseEntity<SuccessResponse> generateResponse(Object data) {
        return ResponseEntity.ok(SuccessResponse.builder()
                .data(data)
                .message("Operation completed successfully")
                .success(true)
                .code(ResponseCode.SUCCESS)
                .build());
    }

    public static ResponseEntity<SuccessResponse> generateResponse(Object data, String message) {
        return ResponseEntity.ok(SuccessResponse.builder()
                .data(data)
                .message(message)
                .success(true)
                .code(ResponseCode.SUCCESS)
                .build());
    }

    public static ResponseEntity<SuccessResponse> generateCreatedResponse(Object data, String message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(SuccessResponse.builder()
                .data(data)
                .message(message)
                .success(true)
                .code(ResponseCode.SUCCESS)
                .build());
    }
}
