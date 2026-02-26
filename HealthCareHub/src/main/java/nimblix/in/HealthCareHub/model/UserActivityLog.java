package nimblix.in.HealthCareHub.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_activity_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String action;

    @Column(length = 1000)
    private String token;

    private LocalDateTime tokenIssuedAt;

    private LocalDateTime tokenExpiresAt;

    @Column(nullable = false)
    private LocalDateTime actionTime;
}
