package ru.patterns.monitoring.application.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.patterns.monitoring.application.service.MonitoringDataService;
import ru.patterns.shared.model.monitoring.LogModel;
import ru.patterns.shared.model.monitoring.RequestMonitoringModel;
import ru.patterns.shared.utility.AuthUtility;

@Controller
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2/add-data")
public class MonitoringDataController {

    private final MonitoringDataService monitoringDataService;

    @PostMapping("/log")
    public void addLog(@RequestBody LogModel log,
                       @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIfEmployee(authorization);

        monitoringDataService.addLog(log);
    }

    @PostMapping("/request")
    public void addRequest(@RequestBody RequestMonitoringModel request,
                           @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIfEmployee(authorization);

        monitoringDataService.addRequest(request);
    }
}
