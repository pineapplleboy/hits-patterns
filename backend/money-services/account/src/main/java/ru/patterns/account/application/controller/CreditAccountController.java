package ru.patterns.account.application.controller;

import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "Получение информации о кредитах пользователя [Сотрудник или Пользователь]")
    public List<CreditAccountShortModel> getUsersCreditsHistory(@PathVariable UUID userId,
                                                                @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return creditAccountService.getUsersCreditsHistory(userId);
    }

    @GetMapping("/history")
    @Operation(summary = "Получение кредитной истории пользователя [Сотрудник или Пользователь]")
    public List<CreditAccountFullModel> getUserCreditHistory(@PathVariable UUID userId,
                                                             @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return creditAccountService.getUsersAllCreditHistory(userId);
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Получение детальной информации о кредите пользователя [Сотрудник или Пользователь]")
    public CreditAccountFullModel getUserCreditFullInfo(@PathVariable UUID userId, @PathVariable String accountNumber,
                                                        @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return creditAccountService.getUserCreditFullInfo(userId, accountNumber);
    }
}
