package nimblix.in.HealthCareHub.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "phone")
    private String phone;

    @Column(name = "qualification")
    private String qualification;

    // Login User (Doctor Account)
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Many Doctors → One Hospital
    @ManyToOne
    @JoinColumn(name = "hospital_id")
    private Hospital hospital;

    @ManyToOne
    @JoinColumn(name = "specialization_id")
    private Specialization specialization;

}
