package ru.patterns.account.application.service.account;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
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
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.kafka.TakeCreditMessage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ExtensionMethod(CreditAccountMapper.class)
public class CreditAccountService {

    private final CreditAccountRepository creditAccountRepository;
    private final BankAccountRepository bankAccountRepository;
    private final OperationService operationService;
    private final OperationHistoryService operationHistoryService;
    private final TransferService transferService;

    public void takeCredit(TakeCreditMessage takeCreditMessage) {
        Optional<BankAccount> bankAccount = bankAccountRepository.
                getBankAccountByAccountNumberAndActiveAndUserId(takeCreditMessage.getBankAccountNumber(), true, takeCreditMessage.getUserId());

        if (bankAccount.isEmpty()) {
            return;
        }

        CreditAccount creditAccount = CreditAccountFactory.createCreditAccount(takeCreditMessage);

        creditAccountRepository.save(creditAccount);

        operationHistoryService.createAndSaveOperation(takeCreditMessage.getUserId(),
                TransferAccountType.CREDIT_ACCOUNT,
                takeCreditMessage.getCreditAmount(),
                AccountActionType.OPEN_ACCOUNT,
                OperationStatus.SUCCESS,
                creditAccount.getAccountNumber());

        transferService.replenishMoney(takeCreditMessage.getUserId(), takeCreditMessage.getBankAccountNumber(),
                new MoneyAmountRequestModel(takeCreditMessage.getCreditAmount()));
    }

    public List<CreditAccountShortModel> getUsersCreditsHistory(UUID userId) {
        return creditAccountRepository.getCreditAccountByUserIdAndActive(userId, true)
                .stream()
                .sorted(Comparator.comparing(CreditAccount::isClosed))
                .map(account -> account.toModel())
                .toList();
    }

    public CreditAccountFullModel getUserCreditFullInfo(UUID userId, String accountNumber) {
        var account = creditAccountRepository.getByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));

        var accountFullModel = account.toFullModel();
        var operations = operationService.getAccountOperations(userId, accountNumber, TransferAccountType.CREDIT_ACCOUNT);

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }
}
