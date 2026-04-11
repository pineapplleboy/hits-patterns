package ru.patterns.shared.exception;

import lombok.Getter;
import ru.patterns.shared.model.log.TracingLog;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final TracingLog logData;

    public UnauthorizedException(String message, TracingLog logData) {
        super(message);

        this.logData = logData;
    }
}
