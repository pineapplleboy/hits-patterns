package ru.patterns.monitoring.application.utility;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;

import java.time.Duration;

@UtilityClass
public class MonitoringFormatUtility {

    public boolean hasKnownServiceId(String serviceId) {
        return StringUtils.hasText(serviceId);
    }

    public String formatServiceId(String serviceId) {
        return serviceId.trim();
    }

    public String formatDuration(Duration duration) {
        if (duration == null) {
            return "0ms";
        }

        long millis = duration.toMillis();
        if (millis > 0) {
            return millis + "ms";
        }

        return duration.toNanos() / 1_000 + "us";
    }
}
