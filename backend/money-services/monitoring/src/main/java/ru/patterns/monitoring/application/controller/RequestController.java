package ru.patterns.monitoring.application.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
import ru.patterns.monitoring.application.utility.RequestTimeParser;
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
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestHeader(value = "traceId", required = false) String traceId,
            HttpServletRequest request
    ) {
        var logData = TraceLogUtility.createDataForLogs(traceId, null, serviceName, request.getRequestURI(), null);
        var parsedStartTime = RequestTimeParser.parse(startTime, "startTime", logData);
        var parsedEndTime = RequestTimeParser.parse(endTime, "endTime", logData);

        return requestService.getRequests(parsedStartTime, parsedEndTime, logData);
    }

    @GetMapping("/average-response-time")
    public List<ServiceAverageResponseTimeModel> getAverageResponseTimeByService(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestHeader(value = "traceId", required = false) String traceId,
            HttpServletRequest request
    ) {
        var logData = TraceLogUtility.createDataForLogs(traceId, null, serviceName, request.getRequestURI(), null);
        var parsedStartTime = RequestTimeParser.parse(startTime, "startTime", logData);
        var parsedEndTime = RequestTimeParser.parse(endTime, "endTime", logData);

        return requestService.getAverageResponseTimeByServices(parsedStartTime, parsedEndTime, logData);
    }

    @GetMapping("/result-percents")
    public List<ServiceRequestResultPercentModel> getRequestResultPercentsByService(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestHeader(value = "traceId", required = false) String traceId,
            HttpServletRequest request
    ) {
        var logData = TraceLogUtility.createDataForLogs(traceId, null, serviceName, request.getRequestURI(), null);
        var parsedStartTime = RequestTimeParser.parse(startTime, "startTime", logData);
        var parsedEndTime = RequestTimeParser.parse(endTime, "endTime", logData);

        return requestService.getRequestResultPercentsByServices(parsedStartTime, parsedEndTime, logData);
    }

    @GetMapping("/requests-per-second")
    public List<ServiceRequestsPerSecondModel> getRequestsPerSecondByService(
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestHeader(value = "traceId", required = false) String traceId,
            HttpServletRequest request
    ) {
        var logData = TraceLogUtility.createDataForLogs(traceId, null, serviceName, request.getRequestURI(), null);
        var parsedStartTime = RequestTimeParser.parse(startTime, "startTime", logData);
        var parsedEndTime = RequestTimeParser.parse(endTime, "endTime", logData);

        return requestService.getRequestsPerSecondByServices(parsedStartTime, parsedEndTime, logData);
    }
}
