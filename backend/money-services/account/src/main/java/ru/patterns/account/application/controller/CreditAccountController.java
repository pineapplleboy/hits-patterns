package ru.patterns.account.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.account.application.common.model.credit.CreditAccountFullModel;
import ru.patterns.account.application.common.model.credit.CreditAccountShortModel;
import ru.patterns.account.application.service.account.CreditAccountService;
import ru.patterns.shared.utility.AuthUtility;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/users/{userId}/credit-accounts")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    @GetMapping
    public List<CreditAccountShortModel> getUsersCreditsHistory(@PathVariable UUID userId, @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return creditAccountService.getUsersCreditsHistory(userId);
    }

    @GetMapping("/{accountNumber}")
    public CreditAccountFullModel getUserCreditFullInfo(@PathVariable UUID userId, @PathVariable String accountNumber,
                                                        @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return creditAccountService.getUserCreditFullInfo(userId, accountNumber);
    }
}
