package ru.patterns.account.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.OperationModel;
import ru.patterns.account.domain.entity.Operation;

import java.util.UUID;

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

    public OperationModel toModel(Operation operation, UUID requestingUser) {
        AccountActionType actionType = operation.getActionType();
        String accountNumberFrom = operation.getAccountNumberFrom();

        if (actionType == AccountActionType.TRANSFER) {
            actionType = requestingUser != null && requestingUser.equals(operation.getUserIdFrom())
                    ? AccountActionType.TRANSFER_RECEIVED
                    : AccountActionType.TRANSFER_SENT;
        }

        return new OperationModel()
                .setOperationId(operation.getOperationId())
                .setAccountNumberFrom(operation.getActionType() == AccountActionType.TRANSFER ? accountNumberFrom : null)
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
