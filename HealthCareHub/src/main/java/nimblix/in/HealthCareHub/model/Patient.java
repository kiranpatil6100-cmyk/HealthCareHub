package nimblix.in.HealthCareHub.model;

import jakarta.persistence.*;
import lombok.*;
import nimblix.in.HealthCareHub.utility.HealthCareUtil;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phoneNo")
    private String phone;

    @Column(name = "disease")
    private String disease;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    // Stored encrypted values for sensitive data
    @Column(name = "government_id_encrypted", length = 2000)
    private String governmentIdEncrypted;

    @Column(name = "medical_history_encrypted", length = 4000)
    private String medicalHistoryEncrypted;

    @Column(name = "insurance_number_encrypted", length = 2000)
    private String insuranceNumberEncrypted;

    @Column(name = "emergency_contact_encrypted", length = 2000)
    private String emergencyContactEncrypted;

    // Plain values are transient and used only during API request/response.
    @Transient
    private String governmentId;

    @Transient
    private String medicalHistory;

    @Transient
    private String insuranceNumber;

    @Transient
    private String emergencyContact;

    // Login User
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Hospital Relationship
    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "updated_time")
    private String updatedTime;


    @PrePersist
    protected void onCreate(){
        createdTime= HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();
        updatedTime= HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();

    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedTime= HealthCareUtil.changeCurrentTimeToLocalDateFromGmtToISTInString();


    }
}
