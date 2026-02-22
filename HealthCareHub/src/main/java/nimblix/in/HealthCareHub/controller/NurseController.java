package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.dto.ApiMessageResponse;
import nimblix.in.HealthCareHub.dto.AuthLoginRequest;
import nimblix.in.HealthCareHub.dto.AuthLoginResponse;
import nimblix.in.HealthCareHub.model.Role;
import nimblix.in.HealthCareHub.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nurse")
@RequiredArgsConstructor
public class NurseController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthLoginRequest request) {
        try {
            AuthLoginResponse response = authService.loginByRole(request, Role.NURSE);
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
    @PreAuthorize("hasRole('NURSE')")
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
}
