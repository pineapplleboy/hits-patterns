package ru.patterns.account.application.service.operation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.entity.CreditAccount;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationHistoryService {

    private final OperationRepository operationRepository;

    public void createAndSaveOperation(UUID userId,
                                       TransferAccountType transferAccountType,
                                       BigDecimal sumFrom,
                                       Integer currencyFrom,
                                       BigDecimal sumTo,
                                       Integer currencyTo,
                                       AccountActionType actionType,
                                       OperationStatus operationStatus,
                                       String accountNumberFrom) {
        Operation operation = new Operation()
                .setAccountNumberFrom(accountNumberFrom)
                .setUserIdFrom(userId)
                .setRecipientAccountNumber(null)
                .setRecipientId(null)
                .setAmountFrom(sumFrom)
                .setCurrencyFrom(currencyFrom)
                .setAmountTo(sumTo)
                .setCurrencyTo(currencyTo)
                .setTransferAccountType(transferAccountType)
                .setActionType(actionType)
                .setStatus(operationStatus);

        operationRepository.save(operation);
    }

    public void createAndSaveOperationAboutAccountCornerOperation(BankAccount account, AccountActionType actionType) {
        Operation operation = new Operation()
                .setAccountNumberFrom(null)
                .setUserIdFrom(account.getUserId())
                .setRecipientAccountNumber(null)
                .setRecipientId(null)
                .setTransferAccountType(TransferAccountType.BANK_ACCOUNT)
                .setActionType(actionType)
                .setStatus(OperationStatus.SUCCESS);

        operationRepository.save(operation);
    }

    public void createAndSaveOperationAboutAccountCornerOperation(CreditAccount account, AccountActionType actionType) {
        Operation operation = new Operation()
                .setAccountNumberFrom(null)
                .setUserIdFrom(account.getUserId())
                .setRecipientAccountNumber(null)
                .setRecipientId(null)
                .setTransferAccountType(TransferAccountType.BANK_ACCOUNT)
                .setActionType(actionType)
                .setStatus(OperationStatus.SUCCESS);

        operationRepository.save(operation);
    }

    public Operation createAndSaveOperation(TransferAssignmentMessage assignmentMessage) {
        Operation operation = new Operation()
                .setOperationId(assignmentMessage.getOperationId())
                .setAccountNumberFrom(assignmentMessage.getAccountNumberFrom())
                .setRecipientAccountNumber(assignmentMessage.getAccountNumberTo())
                .setTransferAccountType(assignmentMessage.getTransferAccountType())
                .setAmountFrom(assignmentMessage.getAmountFrom())
                .setCurrencyFrom(assignmentMessage.getCurrencyFrom())
                .setAmountTo(assignmentMessage.getAmountTo())
                .setCurrencyTo(assignmentMessage.getCurrencyTo())
                .setStatus(assignmentMessage.getStatus())
                .setUserIdFrom(assignmentMessage.getUserIdFrom())
                .setRecipientId(assignmentMessage.getUserIdTo())
                .setActionType(assignmentMessage.getTransferAccountType() == TransferAccountType.BANK_ACCOUNT ?
                        AccountActionType.TRANSFER :
                        AccountActionType.CREDIT_DEPT_PERCENT);

        operationRepository.save(operation);

        return operation;
    }
}
