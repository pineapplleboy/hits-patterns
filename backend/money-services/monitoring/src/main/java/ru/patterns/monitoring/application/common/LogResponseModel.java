package ru.patterns.monitoring.application.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.shared.model.monitoring.LogStatus;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogResponseModel {

    private UUID logId;

    private String serviceId;

    private LogStatus status;

    private String message;

    private String path;

    private String requestBody;

    private String responseBody;

    private UUID requestUserId;

    private String authorization;

    private String traceId;

    private String spanId;

    private Instant logTime;
}
