package ru.patterns.notification.application.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.exception.ForbiddenException;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.exception.UnauthorizedException;
import ru.patterns.shared.model.response.ErrorResponse;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final MonitoringLogger monitoringLogger;

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchNotFoundException(NotFoundException exception) {
        monitoringLogger.logError(exception.getLogData(), exception.getMessage());

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchUnauthorizedException(UnauthorizedException exception) {
        monitoringLogger.logError(exception.getLogData(), exception.getMessage());

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), exception.getMessage()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchForbiddenException(ForbiddenException exception) {
        monitoringLogger.logError(exception.getLogData(), exception.getMessage());

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.FORBIDDEN.value(), exception.getMessage()),
                HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchBadRequestException(BadRequestException exception) {
        monitoringLogger.logError(exception.getLogData(), exception.getMessage());

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchUnknownException(Exception exception) {
        log.error(exception.getMessage(), exception);

        return new ResponseEntity<>(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Произошла непредвиденная ошибка"),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

