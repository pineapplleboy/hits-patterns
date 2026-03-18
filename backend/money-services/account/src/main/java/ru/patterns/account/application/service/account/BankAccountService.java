package ru.patterns.account.application.service.account;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.bankaccount.BankAccountFullModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountShortModel;
import ru.patterns.account.application.common.model.response.AccountNumberResponseModel;
import ru.patterns.account.application.service.currency.CurrencyService;
import ru.patterns.account.application.service.external.SettingsService;
import ru.patterns.account.application.service.operation.OperationHistoryService;
import ru.patterns.account.application.service.operation.OperationService;
import ru.patterns.account.application.utility.CurrencySymbolUtility;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.factory.BankAccountFactory;
import ru.patterns.account.domain.mapper.BankAccountMapper;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.TransferAccountType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@ExtensionMethod(BankAccountMapper.class)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final OperationService operationService;
    private final OperationHistoryService operationHistoryService;
    private final SettingsService settingsService;
    private final CurrencyService currencyService;

    public AccountNumberResponseModel createBankAccount(UUID userId, Integer currencyId) {
        if (!CurrencySymbolUtility.hasCurrency(currencyId)) {
            throw new NotFoundException(ErrorMessages.CURRENCY_NOT_SUPPORTABLE);
        }

        BankAccount bankAccount = BankAccountFactory.createBankAccount(userId, currencyId);
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

    public List<BankAccountShortModel> getAllUserBankAccounts(UUID userId, boolean hidden, String token) {
        List<BankAccount> accountsToShow;

        if (hidden) {
            accountsToShow = settingsService.getListOfHiddenBankAccounts(userId, token);
        }
        else {
            var userBankAccounts = bankAccountRepository.getBankAccountsByUserIdAndActive(userId, true);
            var hiddenUserBankAccounts = settingsService.getHiddenAccountIds(userId, token);

            accountsToShow = userBankAccounts
                    .stream()
                    .filter(account -> !hiddenUserBankAccounts.contains(account.getId()))
                    .toList();
        }

        return accountsToShow
                .stream()
                .map(account -> account.toShortModel(hidden))
                .toList();
    }

    public List<BankAccountShortModel> getAllUserBankAccounts(UUID userId, String token) {
        var userBankAccounts = bankAccountRepository.getBankAccountsByUserIdAndActive(userId, true);

        Set<UUID> hiddenAccountIds = new HashSet<>(
                settingsService.getHiddenAccountIds(userId, token)
        );

        return userBankAccounts
                .stream()
                .map(account -> account.toShortModel(hiddenAccountIds.contains(account.getId())))
                .toList();
    }

    public BankAccountFullModel getBankAccountFullModel(UUID userId, String accountNumber, String token) {
        var account = bankAccountRepository.getBankAccountByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));

        var currency = currencyService.getCurrencyById(account.getCurrencyId(), token);

        var accountFullModel = account.toFullModelWithoutComments(currency);
        var bankOperations = operationService.getAccountOperations(accountNumber, TransferAccountType.BANK_ACCOUNT);

        var creditOperations = operationService.getAccountOperations(accountNumber, TransferAccountType.CREDIT_ACCOUNT);

        var operations = Stream.concat(bankOperations.stream(), creditOperations.stream())
                .sorted((left, right) -> right.getCreateTime().compareTo(left.getCreateTime()))
                .toList();

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }
}
