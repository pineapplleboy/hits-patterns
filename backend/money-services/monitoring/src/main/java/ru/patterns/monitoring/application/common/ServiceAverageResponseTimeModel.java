package ru.patterns.monitoring.application.common;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.Duration;

@Data
@Accessors(chain = true)
public class ServiceAverageResponseTimeModel {

    private String serviceId;

    private Duration averageResponseTime;
}
