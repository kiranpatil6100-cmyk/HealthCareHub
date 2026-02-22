package nimblix.in.HealthCareHub.controller;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.dto.ActiveSessionResponse;
import nimblix.in.HealthCareHub.dto.ApiMessageResponse;
import nimblix.in.HealthCareHub.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ActiveSessionResponse>> myActiveSessions(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(sessionService.getUserActiveSessions(email));
    }

    @PostMapping("/logout-all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiMessageResponse> logoutAllSessions(Authentication authentication) {
        String email = authentication.getName();
        int terminated = sessionService.logoutAllUserSessions(email);
        return ResponseEntity.ok(new ApiMessageResponse("Logged out " + terminated + " active session(s)."));
    }
}
