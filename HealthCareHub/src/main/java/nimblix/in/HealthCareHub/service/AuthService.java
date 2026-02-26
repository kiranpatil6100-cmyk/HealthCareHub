package nimblix.in.HealthCareHub.service;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.dto.AuthLoginRequest;
import nimblix.in.HealthCareHub.dto.AuthLoginResponse;
import nimblix.in.HealthCareHub.dto.AuthRegisterRequest;
import nimblix.in.HealthCareHub.model.BlacklistedToken;
import nimblix.in.HealthCareHub.model.Role;
import nimblix.in.HealthCareHub.model.User;
import nimblix.in.HealthCareHub.model.UserActivityLog;
import nimblix.in.HealthCareHub.repository.BlacklistedTokenRepository;
import nimblix.in.HealthCareHub.repository.UserRepository;
import nimblix.in.HealthCareHub.repository.UserActivityLogRepository;
import nimblix.in.HealthCareHub.security.CustomUserDetailsService;
import nimblix.in.HealthCareHub.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final UserActivityLogRepository userActivityLogRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionService sessionService;

    public AuthLoginResponse login(AuthLoginRequest request) {
        return loginByRole(request, null);
    }

    public AuthLoginResponse loginByRole(AuthLoginRequest request, Role expectedRole) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (expectedRole != null && user.getRole() != expectedRole) {
            throw new IllegalArgumentException(
                    "Access denied. This endpoint is only for " + expectedRole.name() + " users"
            );
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtUtil.generateToken(userDetails);

        LocalDateTime issuedAt = jwtUtil.extractIssuedAt(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime expiresAt = jwtUtil.extractExpiration(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        userActivityLogRepository.save(
                UserActivityLog.builder()
                        .email(email)
                        .action("LOGIN")
                        .token(token)
                        .tokenIssuedAt(issuedAt)
                        .tokenExpiresAt(expiresAt)
                        .actionTime(LocalDateTime.now())
                        .build()
        );

        sessionService.startSession(email, token, expiresAt);

        return AuthLoginResponse.builder()
                .token(token)
                .tokenIssuedAt(issuedAt.format(FORMATTER))
                .tokenExpiresAt(expiresAt.format(FORMATTER))
                .build();
    }

    public void logout(String authHeader) {
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization header with Bearer token is required");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        LocalDateTime issuedAt = jwtUtil.extractIssuedAt(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime expiresAt = jwtUtil.extractExpiration(token).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        blacklistedTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        if (!blacklistedTokenRepository.existsByToken(token)) {
            blacklistedTokenRepository.save(
                    BlacklistedToken.builder()
                            .token(token)
                            .expiresAt(expiresAt)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }

        userActivityLogRepository.save(
                UserActivityLog.builder()
                        .email(email)
                        .action("LOGOUT")
                        .token(token)
                        .tokenIssuedAt(issuedAt)
                        .tokenExpiresAt(expiresAt)
                        .actionTime(LocalDateTime.now())
                        .build()
        );

        sessionService.logoutCurrentToken(token);
    }

    public String register(AuthRegisterRequest request) {
        String email = request.getEmail() == null ? "" : request.getEmail().trim().toLowerCase();

        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("Email is required");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User already exists with this email");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();

        userRepository.save(user);
        return "User created successfully with role " + request.getRole().name();
    }
}
