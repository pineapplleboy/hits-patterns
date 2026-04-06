package ru.patterns.shared.model.monitoring;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;
import java.time.Instant;

@Data
@Accessors(chain=true)
public class RequestMonitoringModel {

    private String path;

    private String serviceId;

    private RequestResult requestResult = RequestResult.OK;

    private Duration responseTime = Duration.ZERO;

    private Instant requestTime = Instant.now();
}
