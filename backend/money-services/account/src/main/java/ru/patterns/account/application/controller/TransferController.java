package ru.patterns.account.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.application.service.TransferService;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.AuthUtility;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/users/{userId}/transfers")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/replenish")
    public OperationStatusResponseModel replenishMoney(@PathVariable UUID userId,
                                                       @RequestBody MoneyAmountRequestModel requestModel,
                                                       @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return transferService.replenishMoney(userId, requestModel);
    }

    @PostMapping("/withdraw")
    public OperationStatusResponseModel withdrawMoney(@PathVariable UUID userId,
                                                       @RequestBody MoneyAmountRequestModel requestModel,
                                                       @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return transferService.withdrawMoney(userId, requestModel);
    }

    @PostMapping("/credit-payments/{creditAccountNumber}")
    public OperationStatusResponseModel payCredit(@PathVariable UUID userId,
                                                  @PathVariable String creditAccountNumber,
                                                  @RequestBody MoneyAmountRequestModel requestModel,
                                                  @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return transferService.payCredit(userId, creditAccountNumber, requestModel);
    }
}
