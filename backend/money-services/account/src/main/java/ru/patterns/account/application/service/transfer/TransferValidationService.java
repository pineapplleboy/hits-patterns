package ru.patterns.account.application.service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.constants.CurrencyConstants;
import ru.patterns.shared.exception.BadRequestException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferValidationService {

    private final BankAccountRepository bankAccountRepository;

    public void checkIfTransferToCreditAccountAvailable(String accountNumber, MoneyAmountRequestModel requestModel) {
        BankAccount bankAccount = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account number not found"));

        validateAccountRemainder(bankAccount, requestModel);

        if (!bankAccount.getCurrencyId().equals(CurrencyConstants.BASE_CURRENCY_ID)) {
            throw new BadRequestException("You can only pay credit with RUB");
        }
    }

    public void checkIfTransferToBankAccountAvailable(String accountNumberFrom, String accountNumberTo, UUID userId,
                                                      UUID recipientId, MoneyAmountRequestModel requestModel) {
        BankAccount bankAccount = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(accountNumberFrom)
                .orElseThrow(() -> new BadRequestException("Account number not found"));

        validateAccountRemainder(bankAccount, requestModel);

        if (accountNumberTo != null && userId != null && recipientId != null && !userId.equals(recipientId)
                && !isBankAccountCurrenciesEquals(bankAccount, accountNumberTo)) {
            throw new BadRequestException("Transfers between bank accounts to different person with different currency are not available");
        }
    }

    private void validateAccountRemainder(BankAccount bankAccount, MoneyAmountRequestModel requestModel) {
        if (bankAccount.isBanned()) {
            throw new BadRequestException("Account is banned");
        }

        if (bankAccount.getBalance().compareTo(requestModel.getAmount()) < 0) {
            throw new BadRequestException("Incorrect request amount");
        }
    }

    private boolean isBankAccountCurrenciesEquals(BankAccount bankAccountFrom, String bankAccountToNumber) {
        BankAccount bankAccountTo = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(bankAccountToNumber)
                .orElseThrow(() -> new BadRequestException("Account number not found"));

        return bankAccountFrom.getCurrencyId().equals(bankAccountTo.getCurrencyId());
    }
}
