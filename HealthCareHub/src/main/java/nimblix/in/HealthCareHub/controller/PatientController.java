package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.dto.ApiMessageResponse;
import nimblix.in.HealthCareHub.dto.AuthLoginRequest;
import nimblix.in.HealthCareHub.dto.AuthLoginResponse;
import nimblix.in.HealthCareHub.dto.PatientCreateRequest;
import nimblix.in.HealthCareHub.dto.PatientCreateResponse;
import nimblix.in.HealthCareHub.dto.PatientExportResponse;
import nimblix.in.HealthCareHub.model.Role;
import nimblix.in.HealthCareHub.service.AuthService;
import nimblix.in.HealthCareHub.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest request) {
        try {
            AuthLoginResponse response = authService.loginByRole(request, Role.PATIENT);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiMessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiMessageResponse("Invalid email or password"));
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiMessageResponse> logout(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            authService.logout(authorizationHeader);
            return ResponseEntity.ok(new ApiMessageResponse("Logout successful. Token expired."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiMessageResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiMessageResponse("Invalid or expired token"));
        }
    }

    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public ResponseEntity<?> createPatient(@RequestBody PatientCreateRequest request) {
        try {
            PatientCreateResponse response = patientService.createPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiMessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{patientId}/export")
    @PreAuthorize("hasAnyRole('DOCTOR','NURSE')")
    public ResponseEntity<?> exportPatientData(@PathVariable("patientId") Long patientId) {
        try {
            PatientExportResponse response = patientService.exportPatientData(patientId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiMessageResponse(e.getMessage()));
        }
    }
}
