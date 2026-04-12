package ru.patterns.notification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.notification.domain.entity.PushDevice;
import ru.patterns.shared.model.external.Role;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface PushDeviceRepository extends JpaRepository<PushDevice, UUID> {
    Optional<PushDevice> findByFcmToken(String fcmToken);
    boolean existsByUserIdAndActiveTrueAndNotificationsEnabledUntilAfter(UUID userId, Instant instant);
    boolean existsByUserRoleAndActiveTrueAndNotificationsEnabledUntilAfter(Role role, Instant instant);
}
