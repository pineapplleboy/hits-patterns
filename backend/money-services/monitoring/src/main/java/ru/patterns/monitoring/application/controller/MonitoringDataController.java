package ru.patterns.monitoring.application.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.monitoring.application.service.MonitoringDataService;
import ru.patterns.shared.model.monitoring.LogModel;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2/add-data")
public class MonitoringDataController {

    private final MonitoringDataService monitoringDataService;

    @Value("${service.name}")
    private String serviceName;

    @PostMapping("/log")
    public void addLog(@RequestBody LogModel log,
                       @Parameter(hidden = true) @RequestHeader String authorization,
                       @RequestHeader(value = "traceId", required = false) String traceId,
                       HttpServletRequest request) {
        AuthUtility.checkUserIfEmployee(authorization);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        monitoringDataService.addLog(log, logData);
    }

    @PostMapping("/request")
    public void addRequest(@RequestBody RequestMonitoringModel requestModel,
                           @Parameter(hidden = true) @RequestHeader String authorization,
                           @RequestHeader(value = "traceId", required = false) String traceId,
                           HttpServletRequest request) {
        AuthUtility.checkUserIfEmployee(authorization);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        monitoringDataService.addRequest(requestModel, logData);
    }
}
