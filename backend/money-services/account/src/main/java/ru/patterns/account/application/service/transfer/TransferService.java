package ru.patterns.account.application.service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.application.kafka.provider.TransferRequestProvider;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.constants.CurrencyConstants;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.kafka.TransferRequestMessage;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRequestProvider transferRequestProvider;
    private final BankAccountRepository bankAccountRepository;
    private final TransferValidationService transferValidationService;

    public OperationStatusResponseModel replenishMoney(UUID userId, String bankAccountNumber,
                                                       MoneyAmountRequestModel requestModel,
                                                       String token) {
        sendRequest(createOperationTransferRequestContext(null, userId, null, bankAccountNumber, requestModel,
                TransferAccountType.BANK_ACCOUNT), token);

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel withdrawMoney(UUID userId, String bankAccountNumber,
                                                      MoneyAmountRequestModel requestModel,
                                                      String token) {
        transferValidationService.checkIfTransferToBankAccountAvailable(bankAccountNumber, null, userId, null, requestModel);

        sendRequest(createOperationTransferRequestContext(userId, null, bankAccountNumber, null, requestModel,
                TransferAccountType.BANK_ACCOUNT), token);

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel transferToBankAccount(UUID userId, String bankAccountFrom,
                                                              String bankAccountTo, MoneyAmountRequestModel requestModel,
                                                              String token) {
        var recipientId = getRecipientId(bankAccountTo);

        sendRequest(createOperationTransferRequestContext(userId, recipientId, bankAccountFrom, bankAccountTo,
                requestModel, TransferAccountType.BANK_ACCOUNT), token);

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }


    public OperationStatusResponseModel payCredit(UUID userId, String bankAccountNumber,
                                                  String creditAccountNumber,
                                                  MoneyAmountRequestModel requestModel,
                                                  String token) {
        transferValidationService.checkIfTransferToCreditAccountAvailable(bankAccountNumber, requestModel);

        sendRequest(createOperationTransferRequestContext(userId, userId, bankAccountNumber, creditAccountNumber, requestModel,
                        TransferAccountType.CREDIT_ACCOUNT), token);

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    private TransferRequestMessage createOperationTransferRequestContext(UUID userIdFrom, UUID userIdTo, String accountNumberFrom,
                                                            String accountNumberTo, MoneyAmountRequestModel amount,
                                                            TransferAccountType transferAccountType) {
        return new TransferRequestMessage()
                .setAccountNumberFrom(accountNumberFrom)
                .setUserIdFrom(userIdFrom)
                .setAccountNumberTo(accountNumberTo)
                .setUserIdTo(userIdTo)
                .setAmount(amount.getAmount())
                .setCurrencyFrom(accountNumberFrom == null ? CurrencyConstants.BASE_CURRENCY_ID : getAccountCurrency(accountNumberFrom))
                .setCurrencyTo(transferAccountType == TransferAccountType.CREDIT_ACCOUNT ? CurrencyConstants.BASE_CURRENCY_ID : getAccountCurrency(accountNumberTo))
                .setTransferType(transferAccountType);
    }

    private void sendRequest(TransferRequestMessage request, String token) {
        transferRequestProvider.send(request, token);
    }

    private UUID getRecipientId(String bankAccountNumber) {
        var bankAccount = bankAccountRepository.getBankAccountByAccountNumberAndActiveTrue(bankAccountNumber)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));

        return bankAccount.getUserId();
    }

    private Integer getAccountCurrency(String accountNumberFrom) {
        var account = bankAccountRepository.getBankAccountByAccountNumberAndActiveTrue(accountNumberFrom)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));

        return account.getCurrencyId();
    }
}
