package ru.patterns.account.application.service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.exception.BadRequestException;

@Service
@RequiredArgsConstructor
public class TransferOperationService {

    private BankAccountRepository bankAccountRepository;

    public void validateAccountRemainder(String accountNumber, MoneyAmountRequestModel requestModel) {
        BankAccount bankAccount = bankAccountRepository
                .getBankAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account number not found"));

        if (bankAccount.getBalance().compareTo(requestModel.getAmount()) < 0) {
            throw new BadRequestException("Incorrect request amount");
        }
    }

}
