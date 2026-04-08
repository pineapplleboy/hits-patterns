package ru.patterns.shared.monitoring.logger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.monitoring.LogModel;
import ru.patterns.shared.model.monitoring.LogStatus;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonitoringLogger {

    private final KafkaLoggerProvider kafkaLoggerProvider;

    public void logInfo(TracingLog logData, String message) {
        logInfo(logData, message, "-", "-");
    }

    public void logWarn(TracingLog logData, String message) {
        logWarn(logData, message, "-", "-");
    }

    public void logError(TracingLog logData, String message) {
        logError(logData, message, "-", "-");
    }

    public void logDebug(TracingLog logData, String message) {
        logDebug(logData, message, "-", "-");
    }

    public void logInfo(TracingLog logData, String message, String requestBody, String responseBody) {
        log.info(message);

        sendMessage(logData, message, requestBody, responseBody, LogStatus.INFO);
    }

    public void logWarn(TracingLog logData, String message, String requestBody, String responseBody) {
        log.warn(message);

        sendMessage(logData, message, requestBody, responseBody, LogStatus.WARN);
    }

    public void logError(TracingLog logData, String message, String requestBody, String responseBody) {
        log.error(message);

        sendMessage(logData, message, requestBody, responseBody, LogStatus.ERROR);
    }

    public void logDebug(TracingLog logData, String message, String requestBody, String responseBody) {
        log.debug(message);

        sendMessage(logData, message, requestBody, responseBody, LogStatus.DEBUG);
    }

    private void sendMessage(TracingLog logData, String message, String requestBody, String responseBody, LogStatus status) {
        var logModel = new LogModel()
                .setServiceId(logData.getServiceId())
                .setPath(logData.getPath())
                .setStatus(status)
                .setMessage(message)
                .setRequestUserId(logData.getRequestUserId())
                .setAuthorization(logData.getAuthorization())
                .setTraceId(logData.getTraceId())
                .setSpanId(logData.getSpanId())
                .setRequestBody(requestBody)
                .setResponseBody(responseBody);

        kafkaLoggerProvider.send(logModel);
    }
}
