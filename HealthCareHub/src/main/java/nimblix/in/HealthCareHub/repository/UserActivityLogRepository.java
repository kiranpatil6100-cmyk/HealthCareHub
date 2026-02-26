package nimblix.in.HealthCareHub.repository;

import nimblix.in.HealthCareHub.model.UserActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLog, Long> {
}
