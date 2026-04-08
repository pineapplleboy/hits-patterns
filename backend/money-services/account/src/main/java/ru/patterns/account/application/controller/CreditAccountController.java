package ru.patterns.account.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.account.application.common.model.credit.CreditAccountFullModel;
import ru.patterns.account.application.common.model.credit.CreditAccountShortModel;
import ru.patterns.account.application.service.account.CreditAccountService;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2/users/{userId}/credit-accounts")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    @Value("${service.name}")
    private String serviceName;

    @GetMapping
    @Operation(summary = "Получение информации о кредитах пользователя [Сотрудник или Пользователь]")
    public List<CreditAccountShortModel> getUsersCreditsHistory(@PathVariable UUID userId,
                                                                @Parameter(hidden = true) @RequestHeader String authorization,
                                                                @RequestHeader(value = "traceId") String traceId,
                                                                HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return creditAccountService.getUsersCreditsHistory(userId, logData);
    }

    @GetMapping("/history")
    @Operation(summary = "Получение кредитной истории пользователя [Сотрудник или Пользователь]")
    public List<CreditAccountFullModel> getUserCreditHistory(@PathVariable UUID userId,
                                                             @Parameter(hidden = true) @RequestHeader String authorization,
                                                             @RequestHeader(value = "traceId") String traceId,
                                                             HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return creditAccountService.getUsersAllCreditHistory(userId, logData);
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Получение детальной информации о кредите пользователя [Сотрудник или Пользователь]")
    public CreditAccountFullModel getUserCreditFullInfo(@PathVariable UUID userId,
                                                        @PathVariable String accountNumber,
                                                        @Parameter(hidden = true) @RequestHeader String authorization,
                                                        @RequestHeader(value = "traceId") String traceId,
                                                        HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return creditAccountService.getUserCreditFullInfo(userId, accountNumber, logData);
    }
}
