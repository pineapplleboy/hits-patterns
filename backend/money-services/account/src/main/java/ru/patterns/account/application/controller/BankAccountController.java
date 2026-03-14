package ru.patterns.account.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.account.application.common.model.AccountNumberResponseModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountFullModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountShortModel;
import ru.patterns.account.application.service.account.BankAccountService;
import ru.patterns.shared.utility.AuthUtility;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("/users/{userId}/bank-accounts")
    public AccountNumberResponseModel createBankAccount(@PathVariable UUID userId,
                                                        @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return bankAccountService.createBankAccount(userId);
    }

    @DeleteMapping("/users/{userId}/bank-accounts/{accountNumber}")
    public void closeBankAccount(@PathVariable UUID userId, @PathVariable String accountNumber,
                                 @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        bankAccountService.closeBankAccount(userId, accountNumber);
    }

    @GetMapping("/users/{userId}/bank-accounts")
    @Operation(summary = "Для пользователя")
    public List<BankAccountShortModel> getUserBankAccounts(@PathVariable UUID userId, @RequestParam boolean hidden,
                                                           @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return bankAccountService.getAllUserBankAccounts(userId, hidden, authorization);
    }

    @GetMapping("/users/{userId}/bank-accounts/all")
    @Operation(summary = "Для работника")
    public List<BankAccountShortModel> getUserBankAccounts(@PathVariable UUID userId,
                                                           @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIfEmployee(authorization);

        return bankAccountService.getAllUserBankAccounts(userId, authorization);
    }

    @GetMapping("/users/{userId}/bank-accounts/{accountNumber}")
    public BankAccountFullModel getBankAccountInfo(@PathVariable UUID userId, @PathVariable String accountNumber,
                                                   @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return bankAccountService.getBankAccountFullModel(userId, accountNumber);
    }
}
