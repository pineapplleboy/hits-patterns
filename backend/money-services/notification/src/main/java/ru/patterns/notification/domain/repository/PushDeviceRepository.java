package ru.patterns.notification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.notification.domain.entity.PushDevice;

import java.util.Optional;
import java.util.UUID;

public interface PushDeviceRepository extends JpaRepository<PushDevice, UUID> {
    Optional<PushDevice> findByFcmToken(String fcmToken);
}
