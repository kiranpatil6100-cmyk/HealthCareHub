package nimblix.in.HealthCareHub.service;

import lombok.RequiredArgsConstructor;
import nimblix.in.HealthCareHub.dto.PatientCreateRequest;
import nimblix.in.HealthCareHub.dto.PatientCreateResponse;
import nimblix.in.HealthCareHub.dto.PatientExportResponse;
import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.repository.PatientRepository;
import nimblix.in.HealthCareHub.security.DataEncryptionService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PatientService {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    private final PatientRepository patientRepository;
    private final DataEncryptionService dataEncryptionService;

    public PatientCreateResponse createPatient(PatientCreateRequest request) {
        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Patient name is required");
        }
        if (request.getAge() == null || request.getAge() <= 0) {
            throw new IllegalArgumentException("Valid patient age is required");
        }

        Patient patient = Patient.builder()
                .name(request.getName())
                .age(request.getAge())
                .gender(request.getGender())
                .phone(request.getPhone())
                .disease(request.getDisease())
                .email(request.getEmail())
                .address(request.getAddress())
                .dateOfBirth(request.getDateOfBirth())
                .governmentId(dataEncryptionService.encrypt(request.getGovernmentId()))
                .medicalHistory(dataEncryptionService.encrypt(request.getMedicalHistory()))
                .insuranceNumber(dataEncryptionService.encrypt(request.getInsuranceNumber()))
                .emergencyContact(dataEncryptionService.encrypt(request.getEmergencyContact()))
                .build();

        Patient saved = patientRepository.save(patient);
        return PatientCreateResponse.builder()
                .patientId(saved.getId())
                .message("Patient created successfully. Sensitive data stored in encrypted format.")
                .build();
    }

    public PatientExportResponse exportPatientData(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with id " + patientId));

        return PatientExportResponse.builder()
                .patientId(patient.getId())
                .name(patient.getName())
                .age(patient.getAge())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .disease(patient.getDisease())
                .email(patient.getEmail())
                .address(patient.getAddress())
                .dateOfBirth(patient.getDateOfBirth())
                .governmentId(dataEncryptionService.decrypt(patient.getGovernmentId()))
                .medicalHistory(dataEncryptionService.decrypt(patient.getMedicalHistory()))
                .insuranceNumber(dataEncryptionService.decrypt(patient.getInsuranceNumber()))
                .emergencyContact(dataEncryptionService.decrypt(patient.getEmergencyContact()))
                .exportedAt(LocalDateTime.now().format(FORMATTER))
                .build();
    }
}
