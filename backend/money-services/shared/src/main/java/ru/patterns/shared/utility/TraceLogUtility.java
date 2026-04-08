package ru.patterns.shared.utility;

import lombok.experimental.UtilityClass;
import ru.patterns.shared.model.log.TracingLog;

import java.util.UUID;

@UtilityClass
public class TraceLogUtility {

    public TracingLog createDataForLogs(String traceId,
                                        String authorization,
                                        String serviceId) {
        return new TracingLog()
                .setTraceId(traceId)
                .setSpanId(generateSpanId())
                .setAuthorization(authorization)
                .setServiceId(serviceId);
    }

    private String generateSpanId()
    {
        return UUID.randomUUID().toString();
    }
}
