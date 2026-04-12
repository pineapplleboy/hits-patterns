package ru.patterns.account.application.service.account;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.model.credit.CreditAccountFullModel;
import ru.patterns.account.application.common.model.credit.CreditAccountShortModel;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
import ru.patterns.account.application.service.operation.OperationHistoryService;
import ru.patterns.account.application.service.operation.OperationService;
import ru.patterns.account.application.service.transfer.TransferService;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.entity.CreditAccount;
import ru.patterns.account.domain.factory.CreditAccountFactory;
import ru.patterns.account.domain.mapper.CreditAccountMapper;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.account.domain.repository.CreditAccountRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.kafka.TakeCreditMessage;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ExtensionMethod(CreditAccountMapper.class)
public class CreditAccountService {

    private final CreditAccountRepository creditAccountRepository;
    private final BankAccountRepository bankAccountRepository;
    private final OperationService operationService;
    private final OperationHistoryService operationHistoryService;
    private final TransferService transferService;
    private final MonitoringLogger monitoringLogger;

    @Value("${service.name}")
    private String serviceName;

    public void takeCredit(TakeCreditMessage takeCreditMessage, String token) {
        Optional<BankAccount> bankAccount = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveAndUserId(takeCreditMessage.getBankAccountNumber(), true, takeCreditMessage.getUserId());

        if (bankAccount.isEmpty()) {
            monitoringLogger.logError("Не найден банковский счёт для выдачи кредита", serviceName);
            return;
        }

        CreditAccount creditAccount = CreditAccountFactory.createCreditAccount(takeCreditMessage);

        creditAccountRepository.save(creditAccount);

        operationHistoryService.createAndSaveOperationAboutAccountCornerOperation(creditAccount, AccountActionType.OPEN_ACCOUNT);

        transferService.replenishMoney(
                takeCreditMessage.getUserId(),
                takeCreditMessage.getBankAccountNumber(),
                new MoneyAmountRequestModel(takeCreditMessage.getCreditAmount()),
                token
        );
    }

    public List<CreditAccountFullModel> getUsersAllCreditHistory(UUID userId) {
        var creditAccounts = creditAccountRepository.getCreditAccountByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(CreditAccount::isClosed))
                .toList();

        Set<String> accountNumbers = creditAccounts.stream()
                .map(CreditAccount::getAccountNumber)
                .collect(Collectors.toSet());

        var operationsByAccount = operationService.getAccountOperations(accountNumbers, TransferAccountType.CREDIT_ACCOUNT);

        return creditAccounts.stream()
                .map(account -> account.toFullModel()
                        .setOperations(operationsByAccount.getOrDefault(account.getAccountNumber(), List.of())))
                .toList();
    }

    public List<CreditAccountFullModel> getUsersAllCreditHistory(UUID userId, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение полной кредитной истории пользователя");

        return getUsersAllCreditHistory(userId);
    }

    public List<CreditAccountShortModel> getUsersCreditsHistory(UUID userId) {
        return creditAccountRepository.getCreditAccountsByUserIdAndClosedIsFalse(userId)
                .stream()
                .sorted(Comparator.comparing(CreditAccount::isClosed))
                .map(account -> account.toModel())
                .toList();
    }

    public List<CreditAccountShortModel> getUsersCreditsHistory(UUID userId, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение активных кредитов пользователя");

        return getUsersCreditsHistory(userId);
    }

    public CreditAccountFullModel getUserCreditFullInfo(UUID userId, String accountNumber) {
        var account = creditAccountRepository.getByAccountNumberAndUserId(accountNumber, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND, null));

        var accountFullModel = account.toFullModel();
        var operations = operationService.getAccountOperations(accountNumber, TransferAccountType.CREDIT_ACCOUNT);

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }

    public CreditAccountFullModel getUserCreditFullInfo(UUID userId, String accountNumber, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение детальной информации о кредите");

        var account = creditAccountRepository.getByAccountNumberAndUserId(accountNumber, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND, logData));

        var accountFullModel = account.toFullModel();
        var operations = operationService.getAccountOperations(accountNumber, TransferAccountType.CREDIT_ACCOUNT);

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }
}
