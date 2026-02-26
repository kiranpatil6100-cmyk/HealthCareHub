package nimblix.in.HealthCareHub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PatientExportResponse {
    private Long patientId;
    private String name;
    private Integer age;
    private String gender;
    private String phone;
    private String disease;
    private String email;
    private String address;
    private String dateOfBirth;
    private String governmentId;
    private String medicalHistory;
    private String insuranceNumber;
    private String emergencyContact;
    private String exportedAt;
}
