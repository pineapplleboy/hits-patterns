package ru.patterns.shared.utility;

import lombok.experimental.UtilityClass;
import org.springframework.util.StringUtils;
import ru.patterns.shared.model.log.TracingLog;

import java.util.UUID;

@UtilityClass
public class TraceLogUtility {

    public TracingLog createDataForLogs(String traceId,
                                        String authorization,
                                        String serviceId,
                                        String path,
                                        UUID userId) {
        return new TracingLog()
                .setTraceId(resolveTraceId(traceId))
                .setSpanId(generateSpanId())
                .setAuthorization(authorization)
                .setServiceId(serviceId)
                .setPath(path)
                .setRequestUserId(userId);
    }

    private String resolveTraceId(String traceId) {
        if (StringUtils.hasText(traceId)) {
            return traceId;
        }

        return UUID.randomUUID().toString();
    }

    private String generateSpanId()
    {
        return UUID.randomUUID().toString();
    }
}
