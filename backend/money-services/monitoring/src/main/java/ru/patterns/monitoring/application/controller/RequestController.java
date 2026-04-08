package ru.patterns.monitoring.application.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.monitoring.application.common.RequestResponseModel;
import ru.patterns.monitoring.application.common.ServiceAverageResponseTimeModel;
import ru.patterns.monitoring.application.common.ServiceRequestResultPercentModel;
import ru.patterns.monitoring.application.common.ServiceRequestsPerSecondModel;
import ru.patterns.monitoring.application.service.RequestService;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2/requests")
public class RequestController {

    private final RequestService requestService;

    @Value("${service.name}")
    private String serviceName;

    @GetMapping
    public List<RequestResponseModel> getRequests(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @Parameter(hidden = true) @RequestHeader String authorization,
            @RequestHeader(value = "traceId") String traceId
    ) {
        AuthUtility.checkUserIfEmployee(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return requestService.getRequests(startTime, endTime, logData);
    }

    @GetMapping("/average-response-time")
    public List<ServiceAverageResponseTimeModel> getAverageResponseTimeByService(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @Parameter(hidden = true) @RequestHeader String authorization,
            @RequestHeader(value = "traceId") String traceId
    ) {
        AuthUtility.checkUserIfEmployee(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return requestService.getAverageResponseTimeByServices(startTime, endTime, logData);
    }

    @GetMapping("/result-percents")
    public List<ServiceRequestResultPercentModel> getRequestResultPercentsByService(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @Parameter(hidden = true) @RequestHeader String authorization,
            @RequestHeader(value = "traceId") String traceId
    ) {
        AuthUtility.checkUserIfEmployee(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return requestService.getRequestResultPercentsByServices(startTime, endTime, logData);
    }

    @GetMapping("/requests-per-second")
    public List<ServiceRequestsPerSecondModel> getRequestsPerSecondByService(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @Parameter(hidden = true) @RequestHeader String authorization,
            @RequestHeader(value = "traceId") String traceId
    ) {
        AuthUtility.checkUserIfEmployee(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return requestService.getRequestsPerSecondByServices(startTime, endTime, logData);
    }
}
