package ru.patterns.account.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.operation.OperationModel;
import ru.patterns.account.application.utility.CurrencySymbolUtility;
import ru.patterns.account.domain.entity.Operation;

@UtilityClass
public class OperationMapper {

    public OperationModel toModel(Operation operation, String requestingAccountNumber) {
        boolean isIncomingOperation = requestingAccountNumber != null
                && requestingAccountNumber.equals(operation.getRecipientAccountNumber());

        AccountActionType actionType = operation.getActionType();
        String accountNumberFrom = operation.getAccountNumberFrom();

        if (actionType == AccountActionType.TRANSFER) {
            actionType = isIncomingOperation
                    ? AccountActionType.TRANSFER_RECEIVED
                    : AccountActionType.TRANSFER_SENT;
        }

        return new OperationModel()
                .setOperationId(operation.getOperationId())
                .setAccountNumberFrom(accountNumberFrom)
                .setActionType(actionType)
                .setCreateTime(operation.getCreateTime())
                .setAmount(isIncomingOperation ?
                        operation.getAmountTo().toString() + CurrencySymbolUtility.getCurrencySymbol(operation.getCurrencyTo()) :
                        operation.getAmountFrom().toString() + CurrencySymbolUtility.getCurrencySymbol(operation.getCurrencyFrom()))
                .setStatus(operation.getStatus())
                .setTransferAccountType(operation.getTransferAccountType())
                .setStatus(operation.getStatus())
                .setRecipientAccountNumber(operation.getRecipientAccountNumber())
                .setUserIdFrom(operation.getUserIdFrom());
    }
}
