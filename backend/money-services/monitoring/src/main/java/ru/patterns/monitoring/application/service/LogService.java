package ru.patterns.monitoring.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.patterns.monitoring.application.common.LogField;
import ru.patterns.monitoring.application.common.LogResponseModel;
import ru.patterns.monitoring.application.utility.MaskUtility;
import ru.patterns.monitoring.domain.entity.Log;
import ru.patterns.monitoring.domain.repository.LogRepository;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;
    private final MonitoringLogger monitoringLogger;

    public List<LogResponseModel> getLogs(
            Instant startTime,
            Instant endTime,
            String path,
            String serviceId,
            String message,
            UUID requestUserId,
            String traceId,
            String spanId,
            List<String> fields,
            TracingLog logData
    ) {
        if (startTime.isAfter(endTime)) {
            throw new BadRequestException("endTime должно быть позже startTime", logData);
        }

        monitoringLogger.logInfo(logData, buildLogsRequestedMessage(logData));

        var selectedFields = LogField.fromStrings(fields);
        var sort = Sort.by(Sort.Direction.DESC, "logTime");
        var specification = buildQuerySpecification(startTime, endTime, path, serviceId, message, requestUserId, traceId, spanId);

        return logRepository.findAll(specification, sort)
                .stream()
                .map(log -> mapToResponse(log, selectedFields))
                .toList();
    }

    private String buildLogsRequestedMessage(TracingLog logData) {
        return "Получен запрос на получение логов пользователем " + logData.getRequestUserId();
    }

    private Specification<Log> buildQuerySpecification(
            Instant startTime,
            Instant endTime,
            String path,
            String serviceId,
            String message,
            UUID requestUserId,
            String traceId,
            String spanId
    ) {
        return Specification.allOf(
                (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("logTime"), startTime, endTime),
                equalsIfPresent("path", path),
                equalsIfPresent("serviceId", serviceId),
                equalsIfPresent("message", message),
                equalsIfPresent("requestUserId", requestUserId),
                equalsIfPresent("traceId", traceId),
                equalsIfPresent("spanId", spanId)
        );
    }

    private <T> Specification<Log> equalsIfPresent(String fieldName, T value) {
        if (value == null) {
            return null;
        }

        if (value instanceof String stringValue && stringValue.isBlank()) {
            return null;
        }

        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(fieldName), value);
    }

    private LogResponseModel mapToResponse(Log log, Set<LogField> selectedFields) {
        var response = new LogResponseModel();

        if (selectedFields.contains(LogField.LOG_ID)) {
            response.setLogId(log.getLogId());
        }
        if (selectedFields.contains(LogField.SERVICE_ID)) {
            response.setServiceId(log.getServiceId());
        }
        if (selectedFields.contains(LogField.STATUS)) {
            response.setStatus(log.getStatus());
        }
        if (selectedFields.contains(LogField.MESSAGE)) {
            response.setMessage(log.getMessage());
        }
        if (selectedFields.contains(LogField.PATH)) {
            response.setPath(log.getPath());
        }
        if (selectedFields.contains(LogField.REQUEST_BODY)) {
            response.setRequestBody(MaskUtility.maskBody(log.getPath(), log.getRequestBody()));
        }
        if (selectedFields.contains(LogField.RESPONSE_BODY)) {
            response.setResponseBody(MaskUtility.maskBody(log.getPath(), log.getResponseBody()));
        }
        if (selectedFields.contains(LogField.REQUEST_USER_ID)) {
            response.setRequestUserId(log.getRequestUserId());
        }
        if (selectedFields.contains(LogField.AUTHORIZATION)) {
            response.setAuthorization(MaskUtility.maskAuthorization(log.getAuthorization()));
        }
        if (selectedFields.contains(LogField.TRACE_ID)) {
            response.setTraceId(log.getTraceId());
        }
        if (selectedFields.contains(LogField.SPAN_ID)) {
            response.setSpanId(log.getSpanId());
        }
        if (selectedFields.contains(LogField.LOG_TIME)) {
            response.setLogTime(log.getLogTime());
        }

        return response;
    }
}
