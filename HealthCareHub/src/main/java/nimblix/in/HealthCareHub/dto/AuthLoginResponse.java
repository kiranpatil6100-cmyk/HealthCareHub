package nimblix.in.HealthCareHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AuthLoginResponse {
    private String token;
    private String tokenIssuedAt;
    private String tokenExpiresAt;
}
