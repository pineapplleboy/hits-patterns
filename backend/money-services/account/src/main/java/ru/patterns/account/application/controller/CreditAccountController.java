package ru.patterns.account.application.controller;

import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/patterns/api/v2/users/{userId}/credit-accounts")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    @GetMapping
    public List<CreditAccountShortModel> getUsersCreditsHistory(@PathVariable UUID userId,
                                                                @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return creditAccountService.getUsersCreditsHistory(userId);
    }

    @GetMapping("/{accountNumber}")
    public CreditAccountFullModel getUserCreditFullInfo(@PathVariable UUID userId, @PathVariable String accountNumber,
                                                        @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return creditAccountService.getUserCreditFullInfo(userId, accountNumber);
    }
}
