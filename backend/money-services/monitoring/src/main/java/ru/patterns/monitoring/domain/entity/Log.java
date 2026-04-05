package ru.patterns.monitoring.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import ru.patterns.monitoring.application.common.enums.LogStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Log {

    @Id
    private UUID id = UUID.randomUUID();

    private String serviceId = null;

    private LogStatus status = LogStatus.INFO;

    private String message = null;

    private String path = null;

    private String requestBody = null;

    private String responseBody = null;

    private UUID requestUserId = null;

    private String authorization = null;

    private String traceId = null;

    private String spanId = null;

    private Instant logTime = Instant.now();
}
