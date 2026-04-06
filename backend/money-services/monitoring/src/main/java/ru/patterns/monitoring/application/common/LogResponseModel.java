package ru.patterns.monitoring.application.common;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.shared.model.monitoring.LogStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class LogResponseModel {

    private UUID logId = UUID.randomUUID();

    private String serviceId;

    private LogStatus status = LogStatus.INFO;

    private String message;

    private String path;

    private String requestBody;

    private String responseBody;

    private UUID requestUserId;

    private String authorization;

    private String traceId;

    private String spanId;

    private Instant logTime = Instant.now();
}
