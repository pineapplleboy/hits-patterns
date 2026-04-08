package ru.patterns.account.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.patterns.account.application.common.model.response.AccountNumberResponseModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountFullModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountShortModel;
import ru.patterns.account.application.service.account.BankAccountService;
import ru.patterns.shared.utility.AuthUtility;
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
                                                        @RequestHeader(value = "traceId") String traceId) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return bankAccountService.createBankAccount(userId, currencyId, logData);
    }

    @DeleteMapping("/users/{userId}/bank-accounts/{accountNumber}")
    @Operation(summary = "Закрытие счёта [Пользователь]")
    public void closeBankAccount(@PathVariable UUID userId, @PathVariable String accountNumber,
                                 @Parameter(hidden = true) @RequestHeader String authorization,
                                 @RequestHeader(value = "traceId") String traceId) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        bankAccountService.closeBankAccount(userId, accountNumber, logData);
    }

    @GetMapping("/users/{userId}/bank-accounts")
    @Operation(summary = "Получение всех счетов (скрытые/нескрытые) [Пользователь]")
    public List<BankAccountShortModel> getUserBankAccounts(@PathVariable UUID userId, @RequestParam boolean hidden,
                                                           @Parameter(hidden = true) @RequestHeader String authorization,
                                                           @RequestHeader(value = "traceId") String traceId) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return bankAccountService.getAllUserBankAccounts(userId, hidden, authorization, logData);
    }

    @GetMapping("/users/{userId}/bank-accounts/all")
    @Operation(summary = "Получение всех счетов пользователя(скрытые + нескрытые) [Работник]")
    public List<BankAccountShortModel> getUserBankAccounts(@PathVariable UUID userId,
                                                           @Parameter(hidden = true) @RequestHeader String authorization,
                                                           @RequestHeader(value = "traceId") String traceId) {
        AuthUtility.checkUserIfEmployee(authorization);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return bankAccountService.getAllUserBankAccounts(userId, authorization, logData);
    }

    @GetMapping("/users/{userId}/bank-accounts/rub")
    @Operation(summary = "Получение всех рублёвых счетов пользователя [Пользователь]")
    public List<BankAccountShortModel> getUserRubBankAccounts(@PathVariable UUID userId,
                                                              @Parameter(hidden = true) @RequestHeader String authorization,
                                                              @RequestHeader(value = "traceId") String traceId) {
        AuthUtility.checkUserIdEquality(authorization, userId);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return bankAccountService.getAllRubUserBankAccounts(userId, logData);
    }

    @GetMapping("/users/{userId}/bank-accounts/{accountNumber}")
    @Operation(summary = "Получение детальной информации о счёте [Работник или Пользователь]")
    public BankAccountFullModel getBankAccountInfo(@PathVariable UUID userId, @PathVariable String accountNumber,
                                                   @Parameter(hidden = true) @RequestHeader String authorization,
                                                   @RequestHeader(value = "traceId") String traceId) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);
        var logData = TraceLogUtility.createDataForLogs(traceId, authorization, serviceName);

        return bankAccountService.getBankAccountFullModel(userId, accountNumber, authorization, logData);
    }
}
