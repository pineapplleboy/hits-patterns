package ru.patterns.account.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.application.common.model.OperationModel;
import ru.patterns.account.application.service.OperationService;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.utility.AuthUtility;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patterns/api/v1")
public class OperationController {

    private final OperationService operationService;

    @GetMapping("/users/{userId}/operations")
    public List<OperationModel> getUserOperations(@PathVariable UUID userId, @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return operationService.getUserOperations(userId);
    }

    @GetMapping("/account-operations")
    public List<OperationModel> getAccountOperations(@RequestParam UUID userId,
                                                     @RequestParam String accountNumber,
                                                     @RequestParam TransferAccountType transferType,
                                                     @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return operationService.getAccountOperations(userId, accountNumber, transferType);
    }

    @GetMapping("/users/{userId}/operations/{operationId}")
    public OperationModel getOperationInfo(@PathVariable UUID userId, @PathVariable UUID operationId, @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return operationService.getOperationInfo(operationId);
    }

    @GetMapping("/users/{userId}/operations/{operationId}/status")
    public OperationStatusResponseModel getOperationStatus(@PathVariable UUID userId, @PathVariable UUID operationId, @RequestHeader String authorization) {
        AuthUtility.checkUserIdEqualityOrUserEmployee(authorization, userId);

        return operationService.getOperationStatus(operationId);
    }
}
