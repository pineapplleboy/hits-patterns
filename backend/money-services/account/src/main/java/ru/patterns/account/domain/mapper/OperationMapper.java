package ru.patterns.account.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.OperationModel;
import ru.patterns.account.domain.entity.Operation;

@UtilityClass
public class OperationMapper {

    public OperationModel toModel(Operation operation) {
        return new OperationModel()
                .setOperationId(operation.getOperationId())
                .setAccountNumberFrom(operation.getAccountNumberFrom())
                .setActionType(operation.getActionType())
                .setCreateTime(operation.getCreateTime())
                .setAmount(operation.getAmount())
                .setStatus(operation.getStatus())
                .setTransferAccountType(operation.getTransferAccountType())
                .setStatus(operation.getStatus())
                .setRecipientAccountNumber(operation.getRecipientAccountNumber())
                .setUserIdFrom(operation.getUserIdFrom());
    }

    public OperationModel toModel(Operation operation, String requestingAccountNumber) {
        AccountActionType actionType = operation.getActionType();
        String accountNumberFrom = operation.getAccountNumberFrom();

        if (actionType == AccountActionType.TRANSFER) {
            actionType = requestingAccountNumber != null && requestingAccountNumber.equals(operation.getAccountNumberFrom())
                    ? AccountActionType.TRANSFER_SENT
                    : AccountActionType.TRANSFER_RECEIVED;
        }

        return new OperationModel()
                .setOperationId(operation.getOperationId())
                .setAccountNumberFrom(accountNumberFrom)
                .setActionType(actionType)
                .setCreateTime(operation.getCreateTime())
                .setAmount(operation.getAmount())
                .setStatus(operation.getStatus())
                .setTransferAccountType(operation.getTransferAccountType())
                .setStatus(operation.getStatus())
                .setRecipientAccountNumber(operation.getRecipientAccountNumber())
                .setUserIdFrom(operation.getUserIdFrom());
    }
}
