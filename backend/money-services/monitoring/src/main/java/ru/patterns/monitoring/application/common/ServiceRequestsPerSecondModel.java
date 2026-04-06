package ru.patterns.monitoring.application.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServiceRequestsPerSecondModel {

    private String serviceId;

    private double requestsPerSecond;
}
