package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.dto.ApiMessageResponse;
import nimblix.in.HealthCareHub.dto.AuthLoginRequest;
import nimblix.in.HealthCareHub.dto.AuthLoginResponse;
import nimblix.in.HealthCareHub.dto.AuthRegisterRequest;
import nimblix.in.HealthCareHub.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiMessageResponse> register(@RequestBody AuthRegisterRequest request) {
        try {
            String message = authService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiMessageResponse(message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiMessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/logout")
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
