package ru.patterns.account.application.service.account;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.AccountNumberResponseModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountFullModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountShortModel;
import ru.patterns.account.application.service.operation.OperationHistoryService;
import ru.patterns.account.application.service.operation.OperationService;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.factory.BankAccountFactory;
import ru.patterns.account.domain.mapper.BankAccountMapper;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.TransferAccountType;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ExtensionMethod(BankAccountMapper.class)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final OperationService operationService;
    private final OperationHistoryService operationHistoryService;

    public AccountNumberResponseModel createBankAccount(UUID userId) {
        BankAccount bankAccount = BankAccountFactory.createBankAccount(userId);
        bankAccountRepository.save(bankAccount);

        operationHistoryService.createAndSaveOperationAboutAccountCornerOperation(bankAccount, AccountActionType.OPEN_ACCOUNT);

        return new AccountNumberResponseModel(bankAccount.getAccountNumber());
    }

    public void closeBankAccount(UUID userId, String accountNumber) {
        var bankAccount = bankAccountRepository.getBankAccountByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));

        bankAccount.setActive(false);
        bankAccountRepository.save(bankAccount);

        operationHistoryService.createAndSaveOperationAboutAccountCornerOperation(bankAccount, AccountActionType.CLOSE_ACCOUNT);
    }

    public List<BankAccountShortModel> getAllBankAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .filter(BankAccount::isActive)
                .map(account -> account.toShortModel())
                .toList();
    }

    public List<BankAccountShortModel> getAllUserBankAccounts(UUID userId) {
        return bankAccountRepository.getBankAccountsByUserIdAndActive(userId, true)
                .stream()
                .map(account -> account.toShortModel())
                .toList();
    }

    public BankAccountFullModel getBankAccountFullModel(UUID userId, String accountNumber) {
        var account = bankAccountRepository.getBankAccountByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));

        var accountFullModel = account.toFullModelWithoutComments();
        var operations = operationService.getAccountOperations(userId, accountNumber, TransferAccountType.BANK_ACCOUNT);

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }
}
