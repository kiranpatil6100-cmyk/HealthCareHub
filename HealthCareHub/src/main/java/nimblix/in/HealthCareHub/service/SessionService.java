package nimblix.in.HealthCareHub.service;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.dto.ActiveSessionResponse;
import nimblix.in.HealthCareHub.dto.SessionValidationResult;
import nimblix.in.HealthCareHub.model.ActiveSession;
import nimblix.in.HealthCareHub.model.BlacklistedToken;
import nimblix.in.HealthCareHub.repository.ActiveSessionRepository;
import nimblix.in.HealthCareHub.repository.BlacklistedTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    @Value("${session.inactivity.minutes:30}")
    private long sessionInactivityMinutes;

    private final ActiveSessionRepository activeSessionRepository;
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    public void startSession(String email, String token, LocalDateTime expiresAt) {
        activeSessionRepository.findByTokenAndActiveTrue(token).ifPresent(session -> {
            session.setActive(false);
            session.setLogoutReason("REPLACED");
            activeSessionRepository.save(session);
        });

        activeSessionRepository.save(
                ActiveSession.builder()
                        .email(email)
                        .token(token)
                        .loginTime(LocalDateTime.now())
                        .lastActivityTime(LocalDateTime.now())
                        .tokenExpiresAt(expiresAt)
                        .active(true)
                        .logoutReason(null)
                        .build()
        );
    }

    public SessionValidationResult validateAndTouch(String token) {
        Optional<ActiveSession> optionalSession = activeSessionRepository.findByTokenAndActiveTrue(token);
        if (optionalSession.isEmpty()) {
            return new SessionValidationResult(false, "Session is not active. Please login again.");
        }

        ActiveSession session = optionalSession.get();
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(session.getTokenExpiresAt())) {
            invalidateSession(session, "TOKEN_EXPIRED");
            return new SessionValidationResult(false, "Token is expired. Please login again.");
        }

        LocalDateTime inactivityLimit = session.getLastActivityTime().plusMinutes(sessionInactivityMinutes);
        if (now.isAfter(inactivityLimit)) {
            invalidateSession(session, "AUTO_LOGOUT_INACTIVITY");
            return new SessionValidationResult(false, "Session expired due to inactivity. Please login again.");
        }

        session.setLastActivityTime(now);
        activeSessionRepository.save(session);
        return new SessionValidationResult(true, null);
    }

    public void logoutCurrentToken(String token) {
        activeSessionRepository.findByTokenAndActiveTrue(token)
                .ifPresent(session -> invalidateSession(session, "LOGOUT"));
    }

    public int logoutAllUserSessions(String email) {
        List<ActiveSession> sessions = activeSessionRepository.findByEmailAndActiveTrue(email);
        sessions.forEach(session -> invalidateSession(session, "LOGOUT_ALL"));
        return sessions.size();
    }

    public List<ActiveSessionResponse> getUserActiveSessions(String email) {
        return activeSessionRepository.findByEmailAndActiveTrue(email)
                .stream()
                .map(session -> ActiveSessionResponse.builder()
                        .email(session.getEmail())
                        .loginTime(format(session.getLoginTime()))
                        .lastActivityTime(format(session.getLastActivityTime()))
                        .tokenExpiresAt(format(session.getTokenExpiresAt()))
                        .active(session.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    private void invalidateSession(ActiveSession session, String reason) {
        blacklistedTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        if (!blacklistedTokenRepository.existsByToken(session.getToken())) {
            blacklistedTokenRepository.save(
                    BlacklistedToken.builder()
                            .token(session.getToken())
                            .expiresAt(session.getTokenExpiresAt())
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }

        session.setActive(false);
        session.setLogoutReason(reason);
        session.setLastActivityTime(LocalDateTime.now());
        activeSessionRepository.save(session);
    }

    private String format(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(FORMATTER);
    }
}
