package co.clinic.appointment.controller;

import co.clinic.appointment.dto.request.PractitionerRequest;
import co.clinic.appointment.response.SuccessResponse;
import co.clinic.appointment.response.SuccessResponseHandler;
import co.clinic.appointment.service.PractitionerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/practitioners")
@Tag(name = "Practitioners", description = "Practitioner management endpoints")
public class PractitionerController {

    private final PractitionerService practitionerService;

    public PractitionerController(PractitionerService practitionerService) {
        this.practitionerService = practitionerService;
    }

    @PostMapping
    @Operation(summary = "Create a new practitioner")
    public ResponseEntity<SuccessResponse> create(@Valid @RequestBody PractitionerRequest request) {
        return SuccessResponseHandler.generateCreatedResponse(
                practitionerService.create(request), "Practitioner created successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get practitioner by ID")
    public ResponseEntity<SuccessResponse> getById(@PathVariable String id) {
        return SuccessResponseHandler.generateResponse(practitionerService.getById(id));
    }

    @GetMapping
    @Operation(summary = "List practitioners — all params optional; filter by specialty or list all")
    public ResponseEntity<SuccessResponse> getAll(
            @RequestParam(required = false) String specialty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return SuccessResponseHandler.generateResponse(
                practitionerService.getAll(specialty, page, size));
    }
}
