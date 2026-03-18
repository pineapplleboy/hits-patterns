package ru.patterns.account.application.service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.constants.CurrencyConstants;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.BadRequestException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferValidationService {

    private final BankAccountRepository bankAccountRepository;

    public void checkIfTransferToCreditAccountAvailable(String accountNumber, MoneyAmountRequestModel requestModel) {
        BankAccount bankAccount = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(accountNumber)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.ACCOUNT_NOT_FOUND));

        validateAccountRemainder(bankAccount, requestModel);

        if (!bankAccount.getCurrencyId().equals(CurrencyConstants.BASE_CURRENCY_ID)) {
            throw new BadRequestException(ErrorMessages.ONLY_WITH_RUB);
        }
    }

    public void checkIfTransferToBankAccountAvailable(String accountNumberFrom, String accountNumberTo, UUID userId,
                                                      UUID recipientId, MoneyAmountRequestModel requestModel) {
        BankAccount bankAccount = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(accountNumberFrom)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.ACCOUNT_NOT_FOUND));

        validateAccountRemainder(bankAccount, requestModel);

        if (accountNumberTo != null && userId != null && recipientId != null && !userId.equals(recipientId)
                && !isBankAccountCurrenciesEquals(bankAccount, accountNumberTo)) {
            throw new BadRequestException(ErrorMessages.TRANSFERS_BETWEEN_CURRENCIES_NOT_AVAILABLE);
        }
    }

    private void validateAccountRemainder(BankAccount bankAccount, MoneyAmountRequestModel requestModel) {
        if (bankAccount.isBanned()) {
            throw new BadRequestException(ErrorMessages.ACCOUNT_BANNED);
        }

        if (bankAccount.getBalance().compareTo(requestModel.getAmount()) < 0) {
            throw new BadRequestException(ErrorMessages.INCORRECT_REQUEST_AMOUNT);
        }
    }

    private boolean isBankAccountCurrenciesEquals(BankAccount bankAccountFrom, String bankAccountToNumber) {
        BankAccount bankAccountTo = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(bankAccountToNumber)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.ACCOUNT_NOT_FOUND));

        return bankAccountFrom.getCurrencyId().equals(bankAccountTo.getCurrencyId());
    }
}
