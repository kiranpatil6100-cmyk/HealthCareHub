package nimblix.in.HealthCareHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ActiveSessionResponse {
    private String email;
    private String loginTime;
    private String lastActivityTime;
    private String tokenExpiresAt;
    private boolean active;
}
