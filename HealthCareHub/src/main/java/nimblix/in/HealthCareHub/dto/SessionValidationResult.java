package nimblix.in.HealthCareHub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SessionValidationResult {
    private boolean valid;
    private String message;
}
