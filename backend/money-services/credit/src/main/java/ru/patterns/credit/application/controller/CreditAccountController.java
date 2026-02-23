package ru.patterns.credit.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.credit.application.service.CreditAccountService;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/credit-account")
public class CreditAccountController {

    private final CreditAccountService creditAccountService;

    @PostMapping("/take/{userId}/{rateId}")
    public OperationStatusResponseModel takeCredit(@PathVariable UUID userId, @PathVariable UUID rateId) {
        return creditAccountService.takeCredit(userId, rateId);
    }
}
