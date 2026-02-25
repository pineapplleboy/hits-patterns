package ru.patterns.account.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.application.service.transfer.TransferService;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.AuthUtility;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1/users/{userId}/transfers/bank-account/{bankAccountNumber}")
public class TransferController {

    private final TransferService transferService;

    @PostMapping("/replenish")
    public OperationStatusResponseModel replenishMoney(@PathVariable UUID userId,
                                                       @PathVariable String bankAccountNumber,
                                                       @RequestBody MoneyAmountRequestModel requestModel,
                                                       @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return transferService.replenishMoney(userId, bankAccountNumber, requestModel);
    }

    @PostMapping("/withdraw")
    public OperationStatusResponseModel withdrawMoney(@PathVariable UUID userId,
                                                      @PathVariable String bankAccountNumber,
                                                       @RequestBody MoneyAmountRequestModel requestModel,
                                                       @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return transferService.withdrawMoney(userId, bankAccountNumber, requestModel);
    }

    @PostMapping("/credit-payments/{creditAccountNumber}")
    public OperationStatusResponseModel payCredit(@PathVariable UUID userId,
                                                  @PathVariable String bankAccountNumber,
                                                  @PathVariable String creditAccountNumber,
                                                  @RequestBody MoneyAmountRequestModel requestModel,
                                                  @RequestHeader String authorization) {
        AuthUtility.checkUserIdEquality(authorization, userId);

        return transferService.payCredit(userId, bankAccountNumber, creditAccountNumber, requestModel);
    }
}
