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
import ru.patterns.shared.constants.CurrencyConstants;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

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
    private final MonitoringLogger monitoringLogger;

    public AccountNumberResponseModel createBankAccount(UUID userId, Integer currencyId) {
        if (!CurrencySymbolUtility.hasCurrency(currencyId)) {
            throw new NotFoundException(ErrorMessages.CURRENCY_NOT_SUPPORTABLE, null);
        }

        BankAccount bankAccount = BankAccountFactory.createBankAccount(userId, currencyId);
        bankAccountRepository.save(bankAccount);

        operationHistoryService.createAndSaveOperationAboutAccountCornerOperation(bankAccount, AccountActionType.OPEN_ACCOUNT);

        return new AccountNumberResponseModel(bankAccount.getAccountNumber());
    }

    public AccountNumberResponseModel createBankAccount(UUID userId, Integer currencyId, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на открытие банковского счёта");

        if (!CurrencySymbolUtility.hasCurrency(currencyId)) {
            monitoringLogger.logWarn(logData, "Попытка открыть банковский счёт с неподдерживаемой валютой");
            throw new NotFoundException(ErrorMessages.CURRENCY_NOT_SUPPORTABLE, logData);
        }

        BankAccount bankAccount = BankAccountFactory.createBankAccount(userId, currencyId);
        bankAccountRepository.save(bankAccount);

        operationHistoryService.createAndSaveOperationAboutAccountCornerOperation(bankAccount, AccountActionType.OPEN_ACCOUNT);

        return new AccountNumberResponseModel(bankAccount.getAccountNumber());
    }

    public void closeBankAccount(UUID userId, String accountNumber) {
        var bankAccount = bankAccountRepository.getBankAccountByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND, null));

        bankAccount.setActive(false);
        bankAccountRepository.save(bankAccount);

        operationHistoryService.createAndSaveOperationAboutAccountCornerOperation(bankAccount, AccountActionType.CLOSE_ACCOUNT);
    }

    public void closeBankAccount(UUID userId, String accountNumber, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на закрытие банковского счёта");

        var bankAccount = bankAccountRepository.getBankAccountByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND, logData));

        bankAccount.setActive(false);
        bankAccountRepository.save(bankAccount);

        operationHistoryService.createAndSaveOperationAboutAccountCornerOperation(bankAccount, AccountActionType.CLOSE_ACCOUNT);
    }

    public List<BankAccountShortModel> getAllUserBankAccounts(UUID userId, boolean hidden, String token) {
        List<BankAccount> accountsToShow;

        if (hidden) {
            accountsToShow = settingsService.getListOfHiddenBankAccounts(userId, token);
        } else {
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

    public List<BankAccountShortModel> getAllUserBankAccounts(UUID userId, boolean hidden, String token, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение списка банковских счетов пользователя");

        return getAllUserBankAccounts(userId, hidden, token);
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

    public List<BankAccountShortModel> getAllUserBankAccounts(UUID userId, String token, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение всех банковских счетов пользователя");

        return getAllUserBankAccounts(userId, token);
    }

    public List<BankAccountShortModel> getAllRubUserBankAccounts(UUID userId) {
        return bankAccountRepository.getBankAccountsByUserIdAndActive(userId, true)
                .stream()
                .filter(account -> account.getCurrencyId().equals(CurrencyConstants.BASE_CURRENCY_ID))
                .map(account -> account.toShortModel(false))
                .toList();
    }

    public List<BankAccountShortModel> getAllRubUserBankAccounts(UUID userId, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение рублёвых банковских счетов пользователя");

        return getAllRubUserBankAccounts(userId);
    }

    public BankAccountFullModel getBankAccountFullModel(UUID userId, String accountNumber, String token) {
        var account = bankAccountRepository.getBankAccountByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND, null));

        var currency = currencyService.getCurrencyById(account.getCurrencyId(), token);

        var accountFullModel = account.toFullModelWithoutOperations(currency);
        var bankOperations = operationService.getAccountOperations(accountNumber, TransferAccountType.BANK_ACCOUNT);
        var creditOperations = operationService.getAccountOperations(accountNumber, TransferAccountType.CREDIT_ACCOUNT);

        var operations = Stream.concat(bankOperations.stream(), creditOperations.stream())
                .sorted((left, right) -> right.getCreateTime().compareTo(left.getCreateTime()))
                .toList();

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }

    public BankAccountFullModel getBankAccountFullModel(UUID userId, String accountNumber, String token, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение детальной информации о банковском счёте");

        var account = bankAccountRepository.getBankAccountByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND, logData));

        var currency = currencyService.getCurrencyById(account.getCurrencyId(), token);

        var accountFullModel = account.toFullModelWithoutOperations(currency);
        var bankOperations = operationService.getAccountOperations(accountNumber, TransferAccountType.BANK_ACCOUNT);
        var creditOperations = operationService.getAccountOperations(accountNumber, TransferAccountType.CREDIT_ACCOUNT);

        var operations = Stream.concat(bankOperations.stream(), creditOperations.stream())
                .sorted((left, right) -> right.getCreateTime().compareTo(left.getCreateTime()))
                .toList();

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }
}
