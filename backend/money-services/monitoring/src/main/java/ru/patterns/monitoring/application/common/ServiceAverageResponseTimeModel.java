package ru.patterns.monitoring.application.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServiceAverageResponseTimeModel {

    private String serviceId;

    private String averageResponseTime;
}
