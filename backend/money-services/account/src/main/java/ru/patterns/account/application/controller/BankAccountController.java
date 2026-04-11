package ru.patterns.account.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.account.application.common.model.bankaccount.BankAccountFullModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountShortModel;
import ru.patterns.account.application.common.model.response.AccountNumberResponseModel;
import ru.patterns.account.application.service.account.BankAccountService;
import ru.patterns.shared.utility.AuthUtility;
import ru.patterns.shared.utility.JwtAuthUtility;
import ru.patterns.shared.utility.TraceLogUtility;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Value("${service.name}")
    private String serviceName;

    @PostMapping("/users/{userId}/bank-accounts")
    @Operation(summary = "Открытие счёта [Пользователь]")
    public AccountNumberResponseModel createBankAccount(@PathVariable UUID userId,
                                                        @RequestParam Integer currencyId,
                                                        @Parameter(hidden = true) @RequestHeader String authorization,
                                                        @RequestHeader(value = "traceId", required = false) String traceId,
                                                        HttpServletRequest request) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return bankAccountService.createBankAccount(userId, currencyId, logData);
    }

    @DeleteMapping("/users/{userId}/bank-accounts/{accountNumber}")
    @Operation(summary = "Закрытие счёта [Пользователь]")
    public void closeBankAccount(@PathVariable UUID userId,
                                 @PathVariable String accountNumber,
                                 @Parameter(hidden = true) @RequestHeader String authorization,
                                 @RequestHeader(value = "traceId", required = false) String traceId,
                                 HttpServletRequest request) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        bankAccountService.closeBankAccount(userId, accountNumber, logData);
    }

    @GetMapping("/users/{userId}/bank-accounts")
    @Operation(summary = "Получение всех счетов (скрытые/нескрытые) [Пользователь]")
    public List<BankAccountShortModel> getUserBankAccounts(@PathVariable UUID userId,
                                                           @RequestParam boolean hidden,
                                                           @Parameter(hidden = true) @RequestHeader String authorization,
                                                           @RequestHeader(value = "traceId", required = false) String traceId,
                                                           HttpServletRequest request) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return bankAccountService.getAllUserBankAccounts(userId, hidden, authorization, logData);
    }

    @GetMapping("/users/{userId}/bank-accounts/all")
    @Operation(summary = "Получение всех счетов пользователя [Работник]")
    public List<BankAccountShortModel> getUserBankAccounts(@PathVariable UUID userId,
                                                           @Parameter(hidden = true) @RequestHeader String authorization,
                                                           @RequestHeader(value = "traceId", required = false) String traceId,
                                                           HttpServletRequest request) {
        AuthUtility.checkUserIfEmployee(authorization);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return bankAccountService.getAllUserBankAccounts(userId, authorization, logData);
    }

    @GetMapping("/users/{userId}/bank-accounts/rub")
    @Operation(summary = "Получение всех рублёвых счетов пользователя [Пользователь]")
    public List<BankAccountShortModel> getUserRubBankAccounts(@PathVariable UUID userId,
                                                              @Parameter(hidden = true) @RequestHeader String authorization,
                                                              @RequestHeader(value = "traceId", required = false) String traceId,
                                                              HttpServletRequest request) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return bankAccountService.getAllRubUserBankAccounts(userId, logData);
    }

    @GetMapping("/users/{userId}/bank-accounts/{accountNumber}")
    @Operation(summary = "Получение детальной информации о счёте [Работник или Пользователь]")
    public BankAccountFullModel getBankAccountInfo(@PathVariable UUID userId,
                                                   @PathVariable String accountNumber,
                                                   @Parameter(hidden = true) @RequestHeader String authorization,
                                                   @RequestHeader(value = "traceId", required = false) String traceId,
                                                   HttpServletRequest request) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var authUser = JwtAuthUtility.parseAuthorizationHeader(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName, request.getRequestURI(), authUser.userId());

        return bankAccountService.getBankAccountFullModel(userId, accountNumber, authorization, logData);
    }
}
