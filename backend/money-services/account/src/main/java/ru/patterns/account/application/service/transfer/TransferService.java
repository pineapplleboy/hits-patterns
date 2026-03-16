package ru.patterns.account.application.service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.application.kafka.provider.TransferRequestProvider;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.repository.BankAccountRepository;
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
    private final TransferRequestProvider transferRequestProvider;
    private final BankAccountRepository bankAccountRepository;
    private final TransferValidationService transferValidationService;

    public OperationStatusResponseModel replenishMoney(UUID userId, String bankAccountNumber,
                                                       MoneyAmountRequestModel requestModel,
                                                       String token) {
        Operation operation = createOperationTransferRequest(null, userId, null, bankAccountNumber, requestModel,
                TransferAccountType.BANK_ACCOUNT);

        sendRequest(operation, token);

        return new OperationStatusResponseModel(operation.getStatus());
    }

    public OperationStatusResponseModel withdrawMoney(UUID userId, String bankAccountNumber,
                                                      MoneyAmountRequestModel requestModel,
                                                      String token) {
        transferValidationService.checkIfTransferToBankAccountAvailable(bankAccountNumber, null, userId, null, requestModel);

        Operation operation = createOperationTransferRequest(userId, null, bankAccountNumber, null, requestModel,
                TransferAccountType.BANK_ACCOUNT);

        sendRequest(operation, token);

        return new OperationStatusResponseModel(operation.getStatus());
    }

    public OperationStatusResponseModel transferToBankAccount(UUID userId, String bankAccountFrom,
                                                              String bankAccountTo, MoneyAmountRequestModel requestModel,
                                                              String token) {
        var recipientId = getRecipientId(bankAccountTo);

        transferValidationService.checkIfTransferToBankAccountAvailable(bankAccountFrom, bankAccountTo, userId, recipientId, requestModel);

        Operation operation = createOperationTransferRequest(userId, recipientId, bankAccountFrom, bankAccountTo,
                requestModel, TransferAccountType.BANK_ACCOUNT);

        sendRequest(operation, token);

        return new OperationStatusResponseModel(operation.getStatus());
    }


    public OperationStatusResponseModel payCredit(UUID userId, String bankAccountNumber,
                                                  String creditAccountNumber,
                                                  MoneyAmountRequestModel requestModel,
                                                  String token) {
        transferValidationService.checkIfTransferToCreditAccountAvailable(bankAccountNumber, requestModel);

        Operation operation = createOperationTransferRequest(userId, userId, bankAccountNumber, creditAccountNumber, requestModel,
                TransferAccountType.CREDIT_ACCOUNT);

        sendRequest(operation, token);

        return new OperationStatusResponseModel(operation.getStatus());
    }

    private Operation createOperationTransferRequest(UUID userIdFrom, UUID userIdTo, String accountNumberFrom,
                                                     String accountNumberTo, MoneyAmountRequestModel amount,
                                                     TransferAccountType transferAccountType) {
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

    private void sendRequest(Operation operation, String token) {
        transferRequestProvider.send(createRequestContext(operation), token);
    }

    private TransferRequestMessage createRequestContext(Operation operation) {
        return new TransferRequestMessage()
                .setUserIdTo(operation.getUserIdFrom())
                .setUserIdTo(operation.getRecipientId())
                .setOperationId(operation.getOperationId())
                .setAccountNumberFrom(operation.getAccountNumberFrom())
                .setAccountNumberTo(operation.getRecipientAccountNumber())
                .setTransferType(operation.getTransferAccountType())
                .setAmount(operation.getAmount());
    }

    private UUID getRecipientId(String bankAccountNumber) {
        var bankAccount = bankAccountRepository.getBankAccountByAccountNumberAndActiveTrue(bankAccountNumber)
                .orElseThrow(() -> new RuntimeException("Bank account not found"));

        return bankAccount.getUserId();
    }
}
