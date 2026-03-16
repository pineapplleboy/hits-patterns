package ru.patterns.transfers.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.patterns.shared.factory.TransferMessageFactory;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;
import ru.patterns.shared.model.kafka.TransferRequestMessage;
import ru.patterns.transfers.application.kafka.provider.TransferAssignmentProvider;

@Service
@RequiredArgsConstructor
public class TransferRequestService {

    private final TransferAssignmentProvider transferAssignmentProvider;

    @Value("${accounts.master-account-number}")
    private String masterAccountNumber;

    public void processTransferRequest(TransferRequestMessage message, String token) {
        transferAssignmentProvider.send(enrichTransfer(message), token);
    }

    private TransferAssignmentMessage enrichTransfer(TransferRequestMessage message) {
        TransferAssignmentMessage assignment = TransferMessageFactory.createAssignment(message);

        if (assignment.getRepeatAmount() > 0) {
            return assignment;
        }

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
