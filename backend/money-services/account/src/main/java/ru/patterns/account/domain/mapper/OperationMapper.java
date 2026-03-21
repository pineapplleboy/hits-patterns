package ru.patterns.account.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.operation.OperationModel;
import ru.patterns.account.application.common.model.operation.CreditOperationModel;
import ru.patterns.account.application.common.model.operation.OperationUpdateStatusModel;
import ru.patterns.account.application.utility.CurrencySymbolUtility;
import ru.patterns.account.domain.entity.Operation;

import java.time.Instant;
import java.util.Objects;

@UtilityClass
public class OperationMapper {

    private static final String EMPTY_ACCOUNT_NUMBER = "0000-0000-0000-0000";

    public OperationModel toBankAccountOperationModel(Operation operation, String requestingAccountNumber) {
        AccountActionType actionType = operation.getActionType();
        String accountNumberFrom = operation.getAccountNumberFrom();

        boolean isIncomingOperation = requestingAccountNumber != null
                && requestingAccountNumber.equals(operation.getRecipientAccountNumber());

        if (actionType == AccountActionType.TRANSFER) {
            actionType = isIncomingOperation
                    ? AccountActionType.TRANSFER_RECEIVED
                    : AccountActionType.TRANSFER_SENT;
        }

        return new OperationModel()
                .setOperationId(operation.getOperationId())
                .setAccountNumberFrom(normalizeAccountNumber(accountNumberFrom))
                .setActionType(actionType)
                .setCreateTime(operation.getCreateTime())
                .setAmount(isIncomingOperation ?
                        operation.getAmountTo().toString() + CurrencySymbolUtility.getCurrencySymbol(operation.getCurrencyTo()) :
                        operation.getAmountFrom().toString() + CurrencySymbolUtility.getCurrencySymbol(operation.getCurrencyFrom()))
                .setStatus(operation.getStatus())
                .setTransferAccountType(operation.getTransferAccountType())
                .setStatus(operation.getStatus())
                .setRecipientAccountNumber(normalizeAccountNumber(operation.getRecipientAccountNumber()))
                .setUserIdFrom(operation.getUserIdFrom())
                .setOperationResolveTime(operation.getOperationResolveTime());
    }

    public OperationModel toCreditOperationModel(Operation operation, String requestingAccountNumber) {
        boolean isIncomingOperation = requestingAccountNumber != null
                && requestingAccountNumber.equals(operation.getRecipientAccountNumber());

        AccountActionType actionType = operation.getActionType();
        String accountNumberFrom = operation.getAccountNumberFrom();

        if (actionType == AccountActionType.TRANSFER) {
            actionType = isIncomingOperation
                    ? AccountActionType.TRANSFER_RECEIVED
                    : AccountActionType.TRANSFER_SENT;
        }

        var expired = !operation.isPurchased() && operation.getExpectedPaymentDate().isBefore(Instant.now());

        return new CreditOperationModel()
                .setExpired(expired)
                .setDeptLeft(operation.getDeptLeft())
                .setExpectedPaymentDate(operation.getExpectedPaymentDate())
                .setOperationId(operation.getOperationId())
                .setAccountNumberFrom(normalizeAccountNumber(accountNumberFrom))
                .setActionType(actionType)
                .setCreateTime(operation.getCreateTime())
                .setAmount(isIncomingOperation ?
                        operation.getAmountTo().toString() + CurrencySymbolUtility.getCurrencySymbol(operation.getCurrencyTo()) :
                        operation.getAmountFrom().toString() + CurrencySymbolUtility.getCurrencySymbol(operation.getCurrencyFrom()))
                .setStatus(operation.getStatus())
                .setTransferAccountType(operation.getTransferAccountType())
                .setStatus(operation.getStatus())
                .setRecipientAccountNumber(normalizeAccountNumber(operation.getRecipientAccountNumber()))
                .setUserIdFrom(operation.getUserIdFrom())
                .setOperationResolveTime(operation.getOperationResolveTime());
    }

    public OperationUpdateStatusModel toStatusModel(Operation operation) {
        return new OperationUpdateStatusModel()
                .setOperationId(operation.getOperationId())
                .setNewStatus(operation.getStatus());
    }

    private String normalizeAccountNumber(String accountNumber) {
        return Objects.equals(accountNumber, EMPTY_ACCOUNT_NUMBER) ? null : accountNumber;
    }
}
