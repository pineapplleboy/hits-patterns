package ru.patterns.monitoring.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.patterns.shared.model.monitoring.LogStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "log")
@Getter
@Setter
@Accessors(chain = true)
public class Log {

    @Id
    @Column(name = "log_id")
    private UUID logId = UUID.randomUUID();

    @Column(name = "service_id")
    private String serviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LogStatus status = LogStatus.INFO;

    @Column(name = "message")
    private String message;

    @Column(name = "path")
    private String path;

    @Column(name = "request_body")
    private String requestBody;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "request_user_id")
    private UUID requestUserId;

    @Column(name = "authorization")
    private String authorization;

    @Column(name = "trace_id")
    private String traceId;

    @Column(name = "span_id")
    private String spanId;

    @Column(name = "log_time")
    private Instant logTime = Instant.now();
}
