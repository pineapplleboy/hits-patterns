package ru.patterns.shared.model.log;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class TracingLog {

    private String traceId = "";

    private String spanId = "";

    private String authorization = "";

    private String serviceId = "";
}
