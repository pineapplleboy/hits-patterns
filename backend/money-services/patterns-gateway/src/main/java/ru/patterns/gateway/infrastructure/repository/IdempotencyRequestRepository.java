package ru.patterns.gateway.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.gateway.domain.IdempotencyRequest;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRequestRepository extends JpaRepository<IdempotencyRequest, UUID> {
    Optional<IdempotencyRequest> findByUserIdAndMethodAndRouteAndIdempotencyKey(
            UUID userId,
            String method,
            String route,
            String idempotencyKey
    );

    void deleteByRequestTimeBefore(Instant requestTime);
}
