package nimblix.in.HealthCareHub.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "disease")
    private String disease;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    // The fields below can be automatically encrypted/decrypted using an AttributeConverter.
    // @Convert(converter = StringCryptoConverter.class)
    @Column(name = "government_id", length = 2000)
    private String governmentId;

    // @Convert(converter = StringCryptoConverter.class)
    @Column(name = "medical_history", length = 4000)
    private String medicalHistory;

    // @Convert(converter = StringCryptoConverter.class)
    @Column(name = "insurance_number", length = 2000)
    private String insuranceNumber;

    // @Convert(converter = StringCryptoConverter.class)
    @Column(name = "emergency_contact", length = 2000)
    private String emergencyContact;

    // Login User
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Hospital Relationship
    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

}
