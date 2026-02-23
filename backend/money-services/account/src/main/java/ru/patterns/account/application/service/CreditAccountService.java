package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.TransferAccountType;
import ru.patterns.account.application.common.model.credit.CreditAccountFullModel;
import ru.patterns.account.application.common.model.credit.CreditAccountShortModel;
import ru.patterns.account.domain.entity.CreditAccount;
import ru.patterns.account.domain.factory.CreditAccountFactory;
import ru.patterns.account.domain.mapper.CreditAccountMapper;
import ru.patterns.account.domain.repository.CreditAccountRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.kafka.TakeCreditMessage;
import ru.patterns.shared.utility.AuthUtility;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ExtensionMethod(CreditAccountMapper.class)
public class CreditAccountService {

    private final CreditAccountRepository creditAccountRepository;
    private final OperationService operationService;
    private final OperationHistoryService operationHistoryService;

    public void TakeCredit(TakeCreditMessage takeCreditMessage) {
        CreditAccount creditAccount = CreditAccountFactory.createCreditAccount(takeCreditMessage);

        creditAccountRepository.save(creditAccount);

        operationHistoryService.createAndSaveOperationAccountCreation(takeCreditMessage.getUserId(),
                TransferAccountType.BANK_ACCOUNT, takeCreditMessage.getCreditAmount());
    }

    public List<CreditAccountShortModel> getUsersCreditsHistory(AuthUser authUser, UUID userId) {
        AuthUtility.checkUserRights(authUser, userId);

        return creditAccountRepository.getCreditAccountByUserIdAndActive(userId, true)
                .stream()
                .sorted(Comparator.comparing(CreditAccount::isClosed))
                .map(account -> account.toModel())
                .toList();
    }

    public CreditAccountFullModel getUserCreditFullInfo(AuthUser authUser, UUID userId, String accountNumber) {
        AuthUtility.checkUserRights(authUser, userId);

        var account = creditAccountRepository.getByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));

        var accountFullModel = account.toFullModel();
        var operations = operationService.getAccountOperations(authUser, authUser.userId(), accountNumber, TransferAccountType.CREDIT_ACCOUNT);

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }
}
