package nimblix.in.HealthCareHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PatientCreateResponse {
    private Long patientId;
    private String message;
}
