package ru.patterns.monitoring.application.common;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.shared.model.monitoring.RequestResult;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class RequestResponseModel {

    private UUID requestId = UUID.randomUUID();

    private String path;

    private String serviceId;

    private RequestResult requestResult = RequestResult.OK;

    private String responseTime;

    private Instant requestTime = Instant.now();
}
