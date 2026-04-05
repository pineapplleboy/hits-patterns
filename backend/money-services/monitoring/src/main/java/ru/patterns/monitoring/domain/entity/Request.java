package ru.patterns.monitoring.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Request {

    @Id
    private UUID id = UUID.randomUUID();

    private String path = null;

    private String serviceId = null;

    private Duration responseTime = Duration.ZERO;

    private Instant requestTime = Instant.now();
}
