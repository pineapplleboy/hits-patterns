package ru.patterns.monitoring.application.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class RequestResponseModel {

    private UUID requestId = UUID.randomUUID();

    private String path;

    private String serviceId;

    private Duration responseTime = Duration.ZERO;

    private Instant requestTime = Instant.now();
}
