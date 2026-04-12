package ru.patterns.shared.model.log;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(chain=true)
public class TracingLog {

    private String traceId = "";

    private String spanId = "";

    private String authorization = "";

    private String serviceId = "";

    private String path = "";

    private UUID requestUserId = UUID.randomUUID();
}
