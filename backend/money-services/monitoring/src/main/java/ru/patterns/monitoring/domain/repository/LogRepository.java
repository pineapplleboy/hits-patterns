package ru.patterns.monitoring.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.patterns.monitoring.domain.entity.Log;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface LogRepository extends JpaRepository<Log, UUID> {
    void deleteByLogTimeBefore(Instant threshold);
    List<Log> findAllByLogTimeBetweenOrderByLogTimeDesc(Instant startTime, Instant endTime);
}
