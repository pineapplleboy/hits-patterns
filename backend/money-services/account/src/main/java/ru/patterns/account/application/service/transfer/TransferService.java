package ru.patterns.account.application.service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.application.kafka.provider.TransferRequestProvider;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.kafka.TransferRequestMessage;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final OperationRepository operationRepository;
    private final TransferOperationService transferOperationService;
    private final TransferRequestProvider transferRequestProvider;

    public OperationStatusResponseModel replenishMoney(UUID userId, String bankAccountNumber, MoneyAmountRequestModel requestModel) {
        Operation operation = createOperationTransferRequest(null, userId, null, bankAccountNumber, requestModel,
                TransferAccountType.BANK_ACCOUNT);

        sendRequest(operation);

        return new OperationStatusResponseModel(operation.getStatus());
    }

    public OperationStatusResponseModel withdrawMoney(UUID userId, String bankAccountNumber, MoneyAmountRequestModel requestModel) {
        Operation operation = createOperationTransferRequest(userId, null, bankAccountNumber, null, requestModel,
                TransferAccountType.BANK_ACCOUNT);

        sendRequest(operation);

        return new OperationStatusResponseModel(operation.getStatus());
    }

    public OperationStatusResponseModel payCredit(UUID userId, String bankAccountNumber,
                                                  String creditAccountNumber, MoneyAmountRequestModel requestModel) {
        transferOperationService.validateAccountRemainder(bankAccountNumber, requestModel);

        Operation operation = createOperationTransferRequest(userId, userId, bankAccountNumber, creditAccountNumber, requestModel,
                TransferAccountType.CREDIT_ACCOUNT);

        sendRequest(operation);

        return new OperationStatusResponseModel(operation.getStatus());
    }

    private Operation createOperationTransferRequest(UUID userIdFrom, UUID userIdTo, String accountNumberFrom,
                                                     String accountNumberTo, MoneyAmountRequestModel amount,
                                                     TransferAccountType transferAccountType) {

        if (accountNumberFrom != null) {
            transferOperationService.validateAccountRemainder(accountNumberFrom, amount);
        }

        Operation operation = new Operation()
                .setUserIdFrom(userIdFrom)
                .setRecipientId(userIdTo)
                .setAccountNumberFrom(accountNumberFrom)
                .setRecipientAccountNumber(accountNumberTo)
                .setAmount(amount.getAmount())
                .setTransferAccountType(transferAccountType)
                .setActionType(AccountActionType.TRANSFER)
                .setStatus(OperationStatus.CREATED);

        operationRepository.save(operation);

        return operation;
    }

    private void sendRequest(Operation operation) {
        transferRequestProvider.send(createRequestContext(operation));
    }

    private TransferRequestMessage createRequestContext(Operation operation) {
        return new TransferRequestMessage()
                .setOperationId(operation.getOperationId())
                .setAccountNumberFrom(operation.getAccountNumberFrom())
                .setAccountNumberTo(operation.getRecipientAccountNumber())
                .setUserIdFrom(operation.getUserIdFrom())
                .setUserIdTo(operation.getRecipientId())
                .setTransferType(operation.getTransferAccountType())
                .setAmount(operation.getAmount());
    }
}
