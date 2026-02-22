package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.ActiveSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActiveSessionRepository extends JpaRepository<ActiveSession, Long> {
    Optional<ActiveSession> findByTokenAndActiveTrue(String token);
    List<ActiveSession> findByEmailAndActiveTrue(String email);
}
