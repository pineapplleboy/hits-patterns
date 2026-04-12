package ru.patterns.monitoring.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.monitoring.application.common.LogResponseModel;
import ru.patterns.monitoring.application.service.LogService;
import ru.patterns.monitoring.application.utility.RequestTimeParser;
import ru.patterns.shared.utility.TraceLogUtility;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2/logs")
public class LogController {

    private final LogService logService;

    @Value("${service.name}")
    private String serviceName;

    @GetMapping
    public Page<LogResponseModel> getLogs(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) UUID requestUserId,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String spanId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) List<String> fields,
            @RequestHeader(value = "traceId", required = false) String requestTraceId,
            HttpServletRequest request
    ) {
        var logData = TraceLogUtility.createDataForLogs(
                requestTraceId,
                null,
                serviceName,
                request.getRequestURI(),
                null
        );
        var parsedStartTime = RequestTimeParser.parse(startTime, "startTime", logData);
        var parsedEndTime = RequestTimeParser.parse(endTime, "endTime", logData);

        return logService.getLogs(
                parsedStartTime,
                parsedEndTime,
                path,
                serviceId,
                message,
                requestUserId,
                traceId,
                spanId,
                page,
                size,
                fields,
                logData
        );
    }
}
