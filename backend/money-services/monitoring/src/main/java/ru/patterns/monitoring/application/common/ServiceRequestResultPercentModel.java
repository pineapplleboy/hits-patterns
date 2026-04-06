package ru.patterns.monitoring.application.common;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ServiceRequestResultPercentModel {

    private String serviceId;

    private double okPercent;

    private double userErrorPercent;

    private double serverErrorPercent;
}
