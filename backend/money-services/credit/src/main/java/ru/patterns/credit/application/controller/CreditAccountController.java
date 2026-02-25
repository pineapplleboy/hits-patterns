package ru.patterns.credit.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.credit.application.service.CreditAccountService;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.AuthUtility;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/credit-account")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    @PostMapping("/take/{userId}/{rateId}")
    public OperationStatusResponseModel takeCredit(@PathVariable UUID userId, @PathVariable UUID rateId,
                                                   @RequestParam BigDecimal sum, @RequestParam String bankAccountNum,
                                                   @RequestHeader String authorization) {

        AuthUtility.checkUserIdEquality(authorization, userId);

        return creditAccountService.takeCredit(userId, rateId, sum, bankAccountNum);
    }
}
