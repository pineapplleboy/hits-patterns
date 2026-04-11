package ru.patterns.monitoring.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.monitoring.application.common.LogResponseModel;
import ru.patterns.monitoring.application.service.LogService;
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
    public List<LogResponseModel> getLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @RequestParam(required = false) String path,
            @RequestParam(required = false) String serviceId,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) UUID requestUserId,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String spanId,
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

        return logService.getLogs(
                startTime,
                endTime,
                path,
                serviceId,
                message,
                requestUserId,
                traceId,
                spanId,
                fields,
                logData
        );
    }
}
