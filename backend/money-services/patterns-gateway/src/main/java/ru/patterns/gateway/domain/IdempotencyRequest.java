package ru.patterns.gateway.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.patterns.gateway.application.common.IdempotencyStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "idempotency_request")
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class IdempotencyRequest {

    @Id
    @Column(name = "idempotency_request_id")
    private UUID idempotencyRequestId = UUID.randomUUID();

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "route", nullable = false)
    private String route;

    @Column(name = "request_hash", nullable = false)
    private String requestHash;

    @Column(name = "response_code")
    private Integer responseCode;

    @Column(name = "response_body")
    private String responseBody;

    @Column(name = "operation_id", nullable = false)
    private UUID operationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private IdempotencyStatus status;

    @Column(name = "request_time", nullable = false)
    private Instant requestTime = Instant.now();
}
