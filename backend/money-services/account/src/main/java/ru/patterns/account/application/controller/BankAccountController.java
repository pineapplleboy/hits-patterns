package ru.patterns.account.application.controller;

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
@RequestMapping("/patterns/api/v1")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping("/users/{userId}/bank-accounts")
    public AccountNumberResponseModel createBankAccount(@PathVariable UUID userId, @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return bankAccountService.createBankAccount(userId);
    }

    @GetMapping("/bank-accounts")
    public List<BankAccountShortModel> getAllBankAccounts(@RequestHeader String authorization) {
        AuthUtility.checkUserIfEmployee(authorization);

        return bankAccountService.getAllBankAccounts();
    }

    @GetMapping("/users/{userId}/bank-accounts")
    public List<BankAccountShortModel> getUserBankAccounts(@PathVariable UUID userId, @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return bankAccountService.getAllUserBankAccounts(userId);
    }

    @GetMapping("/users/{userId}/bank-accounts/{accountNumber}")
    public BankAccountFullModel getBankAccountInfo(@PathVariable UUID userId, @PathVariable String accountNumber, @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return bankAccountService.getBankAccountFullModel(userId, accountNumber);
    }
}
