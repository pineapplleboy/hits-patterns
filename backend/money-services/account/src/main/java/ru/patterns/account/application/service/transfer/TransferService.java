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
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final TransferRequestProvider transferRequestProvider;
    private final BankAccountRepository bankAccountRepository;
    private final TransferValidationService transferValidationService;
    private final MonitoringLogger monitoringLogger;

    public OperationStatusResponseModel replenishMoney(UUID userId, String bankAccountNumber,
                                                       MoneyAmountRequestModel requestModel,
                                                       String token) {
        sendRequest(
                createOperationTransferRequestContext(
                        null,
                        userId,
                        null,
                        bankAccountNumber,
                        requestModel,
                        TransferAccountType.BANK_ACCOUNT
                ),
                token
        );

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel replenishMoney(UUID userId, String bankAccountNumber,
                                                       MoneyAmountRequestModel requestModel,
                                                       String token,
                                                       TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на пополнение банковского счёта");
        sendRequest(
                createOperationTransferRequestContext(
                        null,
                        userId,
                        null,
                        bankAccountNumber,
                        requestModel,
                        TransferAccountType.BANK_ACCOUNT,
                        logData
                ),
                token,
                logData
        );

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel withdrawMoney(UUID userId, String bankAccountNumber,
                                                      MoneyAmountRequestModel requestModel,
                                                      String token) {
        transferValidationService.checkIfTransferToBankAccountAvailable(bankAccountNumber, null, userId, null, requestModel);

        sendRequest(
                createOperationTransferRequestContext(
                        userId,
                        null,
                        bankAccountNumber,
                        null,
                        requestModel,
                        TransferAccountType.BANK_ACCOUNT
                ),
                token
        );

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel withdrawMoney(UUID userId, String bankAccountNumber,
                                                      MoneyAmountRequestModel requestModel,
                                                      String token,
                                                      TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на снятие денег с банковского счёта");

        transferValidationService.checkIfTransferToBankAccountAvailable(bankAccountNumber, null, userId, null, requestModel, logData);
        sendRequest(
                createOperationTransferRequestContext(
                        userId,
                        null,
                        bankAccountNumber,
                        null,
                        requestModel,
                        TransferAccountType.BANK_ACCOUNT,
                        logData
                ),
                token,
                logData
        );

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel transferToBankAccount(UUID userId, String bankAccountFrom,
                                                              String bankAccountTo, MoneyAmountRequestModel requestModel,
                                                              String token) {
        var recipientId = getRecipientId(bankAccountTo);

        transferValidationService.checkIfTransferToBankAccountAvailable(bankAccountFrom, bankAccountTo, userId, recipientId, requestModel);

        sendRequest(
                createOperationTransferRequestContext(
                        userId,
                        recipientId,
                        bankAccountFrom,
                        bankAccountTo,
                        requestModel,
                        TransferAccountType.BANK_ACCOUNT
                ),
                token
        );

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel transferToBankAccount(UUID userId, String bankAccountFrom,
                                                              String bankAccountTo, MoneyAmountRequestModel requestModel,
                                                              String token,
                                                              TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на перевод между банковскими счетами");

        var recipientId = getRecipientId(bankAccountTo, logData);
        transferValidationService.checkIfTransferToBankAccountAvailable(bankAccountFrom, bankAccountTo, userId, recipientId, requestModel, logData);
        sendRequest(
                createOperationTransferRequestContext(
                        userId,
                        recipientId,
                        bankAccountFrom,
                        bankAccountTo,
                        requestModel,
                        TransferAccountType.BANK_ACCOUNT,
                        logData
                ),
                token,
                logData
        );

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel payCredit(UUID userId, String bankAccountNumber,
                                                  String creditAccountNumber,
                                                  MoneyAmountRequestModel requestModel,
                                                  String token) {
        transferValidationService.checkIfTransferToCreditAccountAvailable(bankAccountNumber, requestModel);

        sendRequest(
                createOperationTransferRequestContext(
                        userId,
                        userId,
                        bankAccountNumber,
                        creditAccountNumber,
                        requestModel,
                        TransferAccountType.CREDIT_ACCOUNT
                ),
                token
        );

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    public OperationStatusResponseModel payCredit(UUID userId, String bankAccountNumber,
                                                  String creditAccountNumber,
                                                  MoneyAmountRequestModel requestModel,
                                                  String token,
                                                  TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на погашение кредита с банковского счёта");

        transferValidationService.checkIfTransferToCreditAccountAvailable(bankAccountNumber, requestModel, logData);
        sendRequest(
                createOperationTransferRequestContext(
                        userId,
                        userId,
                        bankAccountNumber,
                        creditAccountNumber,
                        requestModel,
                        TransferAccountType.CREDIT_ACCOUNT,
                        logData
                ),
                token,
                logData
        );

        return new OperationStatusResponseModel(OperationStatus.CREATED);
    }

    private TransferRequestMessage createOperationTransferRequestContext(UUID userIdFrom, UUID userIdTo, String accountNumberFrom,
                                                                         String accountNumberTo, MoneyAmountRequestModel amount,
                                                                         TransferAccountType transferAccountType) {
        return createOperationTransferRequestContext(
                userIdFrom,
                userIdTo,
                accountNumberFrom,
                accountNumberTo,
                amount,
                transferAccountType,
                null
        );
    }

    private TransferRequestMessage createOperationTransferRequestContext(UUID userIdFrom, UUID userIdTo, String accountNumberFrom,
                                                                         String accountNumberTo, MoneyAmountRequestModel amount,
                                                                         TransferAccountType transferAccountType,
                                                                         TracingLog logData) {
        return new TransferRequestMessage()
                .setAccountNumberFrom(accountNumberFrom)
                .setUserIdFrom(userIdFrom)
                .setAccountNumberTo(accountNumberTo)
                .setUserIdTo(userIdTo)
                .setAmount(amount.getAmount())
                .setCurrencyFrom(accountNumberFrom == null ? CurrencyConstants.BASE_CURRENCY_ID : getAccountCurrency(accountNumberFrom, logData))
                .setCurrencyTo(transferAccountType == TransferAccountType.CREDIT_ACCOUNT || accountNumberTo == null
                        ? CurrencyConstants.BASE_CURRENCY_ID : getAccountCurrency(accountNumberTo, logData))
                .setTransferType(transferAccountType);
    }

    private void sendRequest(TransferRequestMessage request, String token) {
        transferRequestProvider.send(request, token);
    }

    private void sendRequest(TransferRequestMessage request, String token, TracingLog logData) {
        transferRequestProvider.send(request, token, logData == null ? null : logData.getTraceId());
    }

    private UUID getRecipientId(String bankAccountNumber) {
        return getRecipientId(bankAccountNumber, null);
    }

    private UUID getRecipientId(String bankAccountNumber, TracingLog logData) {
        var bankAccount = bankAccountRepository.getBankAccountByAccountNumberAndActiveTrue(bankAccountNumber)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND, logData));

        return bankAccount.getUserId();
    }

    private Integer getAccountCurrency(String accountNumberFrom) {
        return getAccountCurrency(accountNumberFrom, null);
    }

    private Integer getAccountCurrency(String accountNumberFrom, TracingLog logData) {
        var account = bankAccountRepository.getBankAccountByAccountNumberAndActiveTrue(accountNumberFrom)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND, logData));

        return account.getCurrencyId();
    }
}
