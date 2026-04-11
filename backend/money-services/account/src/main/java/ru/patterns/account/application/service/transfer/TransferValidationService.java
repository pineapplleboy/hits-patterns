package ru.patterns.account.application.service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.constants.CurrencyConstants;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.log.TracingLog;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferValidationService {

    private final BankAccountRepository bankAccountRepository;

    public void checkIfTransferToCreditAccountAvailable(String accountNumber, MoneyAmountRequestModel requestModel) {
        checkIfTransferToCreditAccountAvailable(accountNumber, requestModel, null);
    }

    public void checkIfTransferToCreditAccountAvailable(String accountNumber, MoneyAmountRequestModel requestModel, TracingLog logData) {
        BankAccount bankAccount = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(accountNumber)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.ACCOUNT_NOT_FOUND, logData));

        validateAccountRemainder(bankAccount, requestModel, logData);

        if (!bankAccount.getCurrencyId().equals(CurrencyConstants.BASE_CURRENCY_ID)) {
            throw new BadRequestException(ErrorMessages.ONLY_WITH_RUB, logData);
        }
    }

    public void checkIfTransferToBankAccountAvailable(String accountNumberFrom, String accountNumberTo, UUID userId,
                                                      UUID recipientId, MoneyAmountRequestModel requestModel) {
        checkIfTransferToBankAccountAvailable(accountNumberFrom, accountNumberTo, userId, recipientId, requestModel, null);
    }

    public void checkIfTransferToBankAccountAvailable(String accountNumberFrom, String accountNumberTo, UUID userId,
                                                      UUID recipientId, MoneyAmountRequestModel requestModel, TracingLog logData) {
        BankAccount bankAccount = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(accountNumberFrom)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.ACCOUNT_NOT_FOUND, logData));

        validateAccountRemainder(bankAccount, requestModel, logData);

        if (userId != null && recipientId != null && !userId.equals(recipientId)
                && !isBankAccountCurrenciesEquals(bankAccount, accountNumberTo, logData)) {
            throw new BadRequestException(ErrorMessages.TRANSFERS_BETWEEN_CURRENCIES_NOT_AVAILABLE, logData);
        }
    }

    private void validateAccountRemainder(BankAccount bankAccount, MoneyAmountRequestModel requestModel, TracingLog logData) {
        if (bankAccount.isBanned()) {
            throw new BadRequestException(ErrorMessages.ACCOUNT_BANNED, logData);
        }

        if (bankAccount.getBalance().compareTo(requestModel.getAmount()) < 0) {
            throw new BadRequestException(ErrorMessages.INCORRECT_REQUEST_AMOUNT, logData);
        }
    }

    private boolean isBankAccountCurrenciesEquals(BankAccount bankAccountFrom, String bankAccountToNumber, TracingLog logData) {
        BankAccount bankAccountTo = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(bankAccountToNumber)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.ACCOUNT_NOT_FOUND, logData));

        return bankAccountFrom.getCurrencyId().equals(bankAccountTo.getCurrencyId());
    }
}
