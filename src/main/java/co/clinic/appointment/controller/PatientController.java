package co.clinic.appointment.controller;

import co.clinic.appointment.dto.request.PatientRequest;
import co.clinic.appointment.response.SuccessResponse;
import co.clinic.appointment.response.SuccessResponseHandler;
import co.clinic.appointment.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patients", description = "Patient management endpoints")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @Operation(summary = "Create a new patient")
    public ResponseEntity<SuccessResponse> create(@Valid @RequestBody PatientRequest request) {
        return SuccessResponseHandler.generateCreatedResponse(
                patientService.create(request), "Patient created successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID")
    public ResponseEntity<SuccessResponse> getById(@PathVariable String id) {
        return SuccessResponseHandler.generateResponse(patientService.getById(id));
    }

    @GetMapping
    @Operation(summary = "Search patients — all params optional; filter by nationalId or name, or list all")
    public ResponseEntity<SuccessResponse> search(
            @RequestParam(required = false) String nationalId,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return SuccessResponseHandler.generateResponse(
                patientService.search(nationalId, name, page, size));
    }
}
