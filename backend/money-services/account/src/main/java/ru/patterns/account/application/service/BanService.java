package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.service.operation.OperationHistoryService;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.account.domain.repository.CreditAccountRepository;
import ru.patterns.shared.model.enums.OperationStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BanService {

    private final BankAccountRepository bankAccountRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final OperationHistoryService operationHistoryService;

    public void banUserAccounts(UUID userId, boolean ban) {
        var userBankAccounts = bankAccountRepository.getBankAccountsByUserId(userId);
        var userCreditAccounts = creditAccountRepository.getCreditAccountByUserId(userId);

        userBankAccounts.forEach(bankAccount -> {

            if ((!bankAccount.isBanned() && ban) || (bankAccount.isBanned() && !ban)) {
                bankAccount.setBanned(!ban);
                callOperationHistoryService(bankAccount.getUserId(), bankAccount.getAccountNumber(), TransferAccountType.BANK_ACCOUNT, ban);
            }

        });
        userCreditAccounts.forEach(creditAccount -> {

            if ((!creditAccount.isActive() && ban) || (creditAccount.isActive() && !ban)) {
                creditAccount.setActive(!ban);
                callOperationHistoryService(creditAccount.getUserId(), creditAccount.getAccountNumber(), TransferAccountType.CREDIT_ACCOUNT, ban);
            }

        });

        bankAccountRepository.saveAll(userBankAccounts);
        creditAccountRepository.saveAll(userCreditAccounts);
    }

    private void callOperationHistoryService(UUID userId, String accountNumber, TransferAccountType transferAccountType, boolean close) {
        operationHistoryService.createAndSaveOperation(userId,
                transferAccountType,
                BigDecimal.ZERO,
                close ? AccountActionType.ACCOUNT_BANNED : AccountActionType.ACCOUNT_UNBANNED,
                OperationStatus.SUCCESS,
                accountNumber);
    }
}
