package ru.patterns.monitoring.application.common;

import ru.patterns.shared.exception.BadRequestException;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public enum LogField {
    LOG_ID("logId"),
    SERVICE_ID("serviceId"),
    STATUS("status"),
    MESSAGE("message"),
    PATH("path"),
    REQUEST_BODY("requestBody"),
    RESPONSE_BODY("responseBody"),
    REQUEST_USER_ID("requestUserId"),
    AUTHORIZATION("authorization"),
    TRACE_ID("traceId"),
    SPAN_ID("spanId"),
    LOG_TIME("logTime");

    private final String fieldName;

    LogField(String fieldName) {
        this.fieldName = fieldName;
    }

    public static Set<LogField> fromStrings(List<String> fields) {
        if (fields.isEmpty()) {
            return EnumSet.allOf(LogField.class);
        }

        var result = EnumSet.noneOf(LogField.class);
        for (var field : fields) {
            var normalizedField = field.trim();

            var matchedField = Arrays.stream(values())
                    .filter(value -> value.fieldName.equalsIgnoreCase(normalizedField))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Неизвестное поле: " + field));

            result.add(matchedField);
        }

        return result.isEmpty() ? EnumSet.allOf(LogField.class) : result;
    }
}
