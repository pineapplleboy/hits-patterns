package ru.patterns.monitoring.application.utility;

import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.log.TracingLog;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

public final class RequestTimeParser {

    private RequestTimeParser() {
    }

    public static Instant parse(String value, String parameterName, TracingLog logData) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException(parameterName + " обязателен", logData);
        }

        try {
            return Instant.parse(value);
        } catch (DateTimeParseException ignored) {
        }

        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (DateTimeParseException ignored) {
        }

        try {
            return LocalDateTime.parse(value).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException ignored) {
        }

        throw new BadRequestException(
                parameterName + " должен быть в ISO-формате, например 2026-04-04T14:33:00Z или 2026-04-04T14:33",
                logData
        );
    }
}
