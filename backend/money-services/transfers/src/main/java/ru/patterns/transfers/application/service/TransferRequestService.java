package ru.patterns.transfers.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;
import ru.patterns.shared.model.kafka.TransferRequestMessage;
import ru.patterns.transfers.application.kafka.provider.TransferAssignmentProvider;

@Service
@RequiredArgsConstructor
public class TransferRequestService {

    private final TransferAssignmentProvider transferAssignmentProvider;

    @Value("${accounts.master-account-number}")
    private String masterAccountNumber;

    public void processTransferRequest(TransferRequestMessage message) {
        transferAssignmentProvider.send(enrichTransfer(message));
    }

    private TransferAssignmentMessage enrichTransfer(TransferRequestMessage message) {
        TransferAssignmentMessage assignment = new TransferAssignmentMessage()
                .setRequestId(message.getRequestId())
                .setOperationId(message.getOperationId())
                .setTransferAccountType(message.getTransferType())
                .setAmount(message.getAmount());

        if (message.getAccountNumberTo() == null) {
            assignment.setAccountNumberTo(masterAccountNumber);
        } else {
            assignment.setAccountNumberTo(message.getAccountNumberTo());
        }

        if (message.getAccountNumberFrom() == null) {
            assignment.setAccountNumberFrom(masterAccountNumber);
        } else {
            assignment.setAccountNumberFrom(message.getAccountNumberFrom());
        }

        return assignment;
    }
}
