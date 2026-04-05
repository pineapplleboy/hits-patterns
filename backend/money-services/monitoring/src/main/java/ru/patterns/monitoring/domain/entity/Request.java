package ru.patterns.monitoring.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "request")
@Getter
@Setter
@Accessors(chain = true)
public class Request {

    @Id
    @Column(name = "request_id")
    private UUID requestId = UUID.randomUUID();

    @Column(name = "path")
    private String path;

    @Column(name = "service_id")
    private String serviceId;

    @Column(name = "response_time")
    private Duration responseTime = Duration.ZERO;

    @Column(name = "request_time")
    private Instant requestTime = Instant.now();
}
