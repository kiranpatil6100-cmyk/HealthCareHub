package nimblix.in.HealthCareHub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientCreateRequest {
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
}
