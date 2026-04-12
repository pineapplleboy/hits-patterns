package ru.patterns.shared.exception;

import lombok.Getter;
import ru.patterns.shared.model.log.TracingLog;

@Getter
public class BadRequestException extends RuntimeException {

    private final TracingLog logData;

    public BadRequestException(String message) {
        this(message, null);
    }

    public BadRequestException(String message, TracingLog logData) {
        super(message);

        this.logData = logData;
    }
}
