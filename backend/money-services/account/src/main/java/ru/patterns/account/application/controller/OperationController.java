package ru.patterns.account.application.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.application.common.model.operation.OperationModel;
import ru.patterns.account.application.service.operation.OperationService;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.AuthUtility;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v2")
public class OperationController {

    private final OperationService operationService;

    @GetMapping("/users/{userId}/operations")
    @Operation(summary = "Получение операций пользователя [Сотрудник или Пользователь]")
    public List<OperationModel> getUserOperations(@PathVariable UUID userId,
                                                  @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return operationService.getUserOperations(userId);
    }

    @GetMapping("/users/{userId}/operations/{accountNumber}")
    @Operation(summary = "Получение операций пользователя по счёту/кредиту [Сотрудник или Пользователь]")
    public List<OperationModel> getAccountOperations(@PathVariable UUID userId,
                                                     @PathVariable String accountNumber,
                                                     @RequestParam TransferAccountType transferType,
                                                     @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return operationService.getAccountOperations(accountNumber, transferType);
    }

    @GetMapping("/users/{userId}/operations/{operationId}/status")
    @Operation(summary = "Получение статуса операции [Сотрудник или Пользователь]")
    public OperationStatusResponseModel getOperationStatus(@PathVariable UUID userId, @PathVariable UUID operationId,
                                                           @Parameter(hidden = true) @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return operationService.getOperationStatus(operationId);
    }
}
