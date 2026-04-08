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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.account.application.common.model.operation.OperationModel;
import ru.patterns.account.application.service.operation.OperationService;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2")
public class OperationController {

    private final OperationService operationService;

    @Value("${service.name}")
    private String serviceName;

    @GetMapping("/users/{userId}/operations")
    @Operation(summary = "Получение операций пользователя [Сотрудник или Пользователь]")
    public List<OperationModel> getUserOperations(@PathVariable UUID userId,
                                                  @Parameter(hidden = true) @RequestHeader String authorization,
                                                  @RequestHeader(value = "traceId") String traceId,
                                                  HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return operationService.getUserOperations(userId, logData);
    }

    @GetMapping("/users/{userId}/operations/{accountNumber}")
    @Operation(summary = "Получение операций пользователя по счёту/кредиту [Сотрудник или Пользователь]")
    public List<OperationModel> getAccountOperations(@PathVariable UUID userId,
                                                     @PathVariable String accountNumber,
                                                     @RequestParam TransferAccountType transferType,
                                                     @Parameter(hidden = true) @RequestHeader String authorization,
                                                     @RequestHeader(value = "traceId") String traceId,
                                                     HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return operationService.getAccountOperations(accountNumber, transferType, logData);
    }

    @GetMapping("/users/{userId}/operations/{operationId}/status")
    @Operation(summary = "Получение статуса операции [Сотрудник или Пользователь]")
    public OperationStatusResponseModel getOperationStatus(@PathVariable UUID userId,
                                                           @PathVariable UUID operationId,
                                                           @Parameter(hidden = true) @RequestHeader String authorization,
                                                           @RequestHeader(value = "traceId") String traceId,
                                                           HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return operationService.getOperationStatus(operationId, logData);
    }

    @GetMapping("/users/{userId}/operations/missed")
    @Operation(summary = "Получение просроченных платежей по кредитам пользователя [Сотрудник или Пользователь]")
    public List<OperationModel> getExpiredOperations(@PathVariable UUID userId,
                                                     @Parameter(hidden = true) @RequestHeader String authorization,
                                                     @RequestHeader(value = "traceId") String traceId,
                                                     HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return operationService.getExpiredCreditOperations(userId, logData);
    }
}
