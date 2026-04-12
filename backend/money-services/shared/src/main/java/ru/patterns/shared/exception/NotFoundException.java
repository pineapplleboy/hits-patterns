package ru.patterns.shared.exception;

import lombok.Getter;
import ru.patterns.shared.model.log.TracingLog;

@Getter
public class NotFoundException extends RuntimeException {

    private final TracingLog logData;

    public NotFoundException(String message) {
        this(message, null);
    }

    public NotFoundException(String message, TracingLog logData) {
        super(message);

        this.logData = logData;
    }
}
