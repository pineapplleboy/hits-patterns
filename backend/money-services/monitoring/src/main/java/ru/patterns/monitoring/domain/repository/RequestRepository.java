package ru.patterns.monitoring.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.monitoring.domain.entity.Request;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface RequestRepository extends JpaRepository<Request, UUID> {
    void deleteByRequestTimeBefore(Instant threshold);
    List<Request> findAllByRequestTimeBetweenOrderByRequestTimeDesc(Instant startTime, Instant endTime);
}
