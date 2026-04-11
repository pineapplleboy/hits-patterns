package ru.patterns.notification.application.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.patterns.notification.application.common.TokenRequest;
import ru.patterns.notification.application.service.PushDeviceService;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1")
public class PushSubscriptionController {

    private final PushDeviceService pushDeviceService;

    @Value("${service.name}")
    private String serviceName;

    @PostMapping("/register")
    public void registerToken(@RequestBody TokenRequest tokenModel,
                              @Parameter(hidden = true) @RequestHeader String authorization,
                              @RequestHeader(value = "traceId", required = false) String traceId,
                              HttpServletRequest request) {
        AuthUtility.isAuthorized(authorization);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        pushDeviceService.registerToken(tokenModel, JwtAuthUtility.parseAuthorizationHeader(authorization), logData);
    }

    @PostMapping("/unsubscribe")
    public void unsubscribeToken(@RequestBody TokenRequest tokenModel,
                                 @Parameter(hidden = true) @RequestHeader String authorization,
                                 @RequestHeader(value = "traceId", required = false) String traceId,
                                 HttpServletRequest request) {
        AuthUtility.isAuthorized(authorization);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        pushDeviceService.unsubscribeToken(tokenModel, JwtAuthUtility.parseAuthorizationHeader(authorization), logData);
    }

}
