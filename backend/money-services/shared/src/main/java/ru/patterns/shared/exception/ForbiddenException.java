package ru.patterns.shared.exception;

import lombok.Getter;
import ru.patterns.shared.model.log.TracingLog;

@Getter
public class ForbiddenException extends RuntimeException {

    private final TracingLog logData;

    public ForbiddenException(String message, TracingLog logData) {
        super(message);

        this.logData = logData;
    }
}
