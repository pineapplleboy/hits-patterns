package ru.patterns.monitoring.application.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.monitoring.application.common.LogResponseModel;
import ru.patterns.monitoring.application.service.LogService;
import ru.patterns.shared.utility.AuthUtility;

import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2/logs")
public class LogController {

    private final LogService logService;

    @GetMapping
    public List<LogResponseModel> getLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime,
            @Parameter(hidden = true) @RequestHeader String authorization
    ) {
        AuthUtility.checkUserIfEmployee(authorization);

        return logService.getLogs(startTime, endTime);
    }
}
