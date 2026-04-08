package ru.patterns.account.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.application.service.transfer.TransferService;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2/users/{userId}/transfers/bank-account/{bankAccountNumber}")
public class TransferController {

    private final TransferService transferService;

    @Value("${service.name}")
    private String serviceName;

    @PostMapping("/replenish")
    @Operation(summary = "Пополнить счёт [Пользователь]")
    public OperationStatusResponseModel replenishMoney(@PathVariable UUID userId,
                                                       @PathVariable String bankAccountNumber,
                                                       @RequestBody MoneyAmountRequestModel requestModel,
                                                       @Parameter(hidden = true) @RequestHeader String authorization,
                                                       @RequestHeader(value = "traceId") String traceId,
                                                       HttpServletRequest request) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return transferService.replenishMoney(userId, bankAccountNumber, requestModel, authorization, logData);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Снять деньги со счёта [Пользователь]")
    public OperationStatusResponseModel withdrawMoney(@PathVariable UUID userId,
                                                      @PathVariable String bankAccountNumber,
                                                      @RequestBody MoneyAmountRequestModel requestModel,
                                                      @Parameter(hidden = true) @RequestHeader String authorization,
                                                      @RequestHeader(value = "traceId") String traceId,
                                                      HttpServletRequest request) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return transferService.withdrawMoney(userId, bankAccountNumber, requestModel, authorization, logData);
    }

    @PostMapping("/credit-payments/{creditAccountNumber}")
    @Operation(summary = "Перевести деньги на кредит [Пользователь]")
    public OperationStatusResponseModel payCredit(@PathVariable UUID userId,
                                                  @PathVariable String bankAccountNumber,
                                                  @PathVariable String creditAccountNumber,
                                                  @RequestBody MoneyAmountRequestModel requestModel,
                                                  @Parameter(hidden = true) @RequestHeader String authorization,
                                                  @RequestHeader(value = "traceId") String traceId,
                                                  HttpServletRequest request) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return transferService.payCredit(userId, bankAccountNumber, creditAccountNumber, requestModel, authorization, logData);
    }

    @PostMapping("/bankAccountTo/{bankAccountTo}")
    @Operation(summary = "Перевод на другой счёт")
    public OperationStatusResponseModel transferToBankAccount(@PathVariable UUID userId,
                                                              @PathVariable String bankAccountNumber,
                                                              @PathVariable String bankAccountTo,
                                                              @RequestBody MoneyAmountRequestModel requestModel,
                                                              @Parameter(hidden = true) @RequestHeader String authorization,
                                                              @RequestHeader(value = "traceId") String traceId,
                                                              HttpServletRequest request) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return transferService.transferToBankAccount(userId, bankAccountNumber, bankAccountTo, requestModel, authorization, logData);
    }
}
