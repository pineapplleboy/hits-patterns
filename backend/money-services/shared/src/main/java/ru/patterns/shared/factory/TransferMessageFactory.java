package ru.patterns.shared.factory;

import lombok.experimental.UtilityClass;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;
import ru.patterns.shared.model.kafka.TransferRequestMessage;

@UtilityClass
public class TransferMessageFactory {

    public TransferRequestMessage createRepeatRequest(TransferAssignmentMessage msg) {
        return new TransferRequestMessage()
                .setRequestId(msg.getRequestId())
                .setOperationId(msg.getOperationId())
                .setTransferType(msg.getTransferAccountType())
                .setAmount(msg.getAmountFrom())
                .setAccountNumberFrom(msg.getAccountNumberFrom())
                .setAccountNumberTo(msg.getAccountNumberTo())
                .setRepeatAmount(msg.getRepeatAmount() + 1);
    }

    public TransferAssignmentMessage createAssignment(TransferRequestMessage msg) {
        return new TransferAssignmentMessage()
                .setRequestId(msg.getRequestId())
                .setOperationId(msg.getOperationId())
                .setTransferAccountType(msg.getTransferType())
                .setAccountNumberFrom(msg.getAccountNumberFrom())
                .setAccountNumberTo(msg.getAccountNumberTo())
                .setCurrencyFrom(msg.getCurrencyFrom())
                .setCurrencyTo(msg.getCurrencyTo())
                .setRepeatAmount(msg.getRepeatAmount())
                .setUserIdFrom(msg.getUserIdFrom())
                .setUserIdTo(msg.getUserIdTo());
    }
}
