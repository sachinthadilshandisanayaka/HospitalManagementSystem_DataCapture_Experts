package co.clinic.appointment.controller;

import co.clinic.appointment.dto.request.AppointmentRequest;
import co.clinic.appointment.dto.request.UpdateNotesRequest;
import co.clinic.appointment.util.AppointmentStatus;
import co.clinic.appointment.response.SuccessResponse;
import co.clinic.appointment.response.SuccessResponseHandler;
import co.clinic.appointment.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Appointment management endpoints")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @Operation(summary = "Create a new appointment (validates conflict rule)")
    public ResponseEntity<SuccessResponse> create(@Valid @RequestBody AppointmentRequest request) {
        return SuccessResponseHandler.generateCreatedResponse(
                appointmentService.create(request), "Appointment booked successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<SuccessResponse> getById(@PathVariable String id) {
        return SuccessResponseHandler.generateResponse(appointmentService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List appointments — all params optional; filter by practitionerId, date, and/or status")
    public ResponseEntity<SuccessResponse> list(
            @RequestParam(required = false) String practitionerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return SuccessResponseHandler.generateResponse(
                appointmentService.list(practitionerId, date, status, page, size));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a BOOKED appointment")
    public ResponseEntity<SuccessResponse> cancel(@PathVariable String id) {
        return SuccessResponseHandler.generateResponse(
                appointmentService.cancel(id), "Appointment cancelled successfully");
    }

    @PatchMapping("/{id}/complete")
    @Operation(summary = "Mark a BOOKED appointment as COMPLETED")
    public ResponseEntity<SuccessResponse> complete(@PathVariable String id) {
        return SuccessResponseHandler.generateResponse(
                appointmentService.complete(id), "Appointment marked as completed");
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update appointment notes")
    public ResponseEntity<SuccessResponse> updateNotes(
            @PathVariable String id,
            @Valid @RequestBody UpdateNotesRequest request) {
        return SuccessResponseHandler.generateResponse(
                appointmentService.updateNotes(id, request), "Appointment notes updated");
    }
}
