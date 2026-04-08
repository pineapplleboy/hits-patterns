package ru.patterns.monitoring.application.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.patterns.monitoring.application.service.MonitoringDataService;
import ru.patterns.shared.model.monitoring.LogModel;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;
import ru.patterns.shared.utility.AuthUtility;
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
                       @RequestHeader(value = "traceId") String traceId) {
        AuthUtility.checkUserIfEmployee(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        monitoringDataService.addLog(log, logData);
    }

    @PostMapping("/request")
    public void addRequest(@RequestBody RequestMonitoringModel request,
                           @Parameter(hidden = true) @RequestHeader String authorization,
                           @RequestHeader(value = "traceId") String traceId) {
        AuthUtility.checkUserIfEmployee(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        monitoringDataService.addRequest(request, logData);
    }
}
