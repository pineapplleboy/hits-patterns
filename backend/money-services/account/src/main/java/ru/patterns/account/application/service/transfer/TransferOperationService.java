package ru.patterns.account.application.service.transfer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.enums.TransactionFinishStatus;
import ru.patterns.account.application.service.operation.OperationHistoryService;
import ru.patterns.account.application.service.websocket.OperationWebSocketPublisher;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.entity.CreditAccount;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.account.domain.repository.CreditAccountRepository;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferOperationService {

    private final BankAccountRepository bankAccountRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final OperationRepository operationRepository;
    private final OperationHistoryService operationHistoryService;
    private final OperationWebSocketPublisher operationWebSocketPublisher;

    @Value("${master-bank.account-number}")
    private String masterBankAccountNumber;

    @Transactional
    public TransactionFinishStatus makeTransfer(TransferAssignmentMessage assignment) {
        BankAccount bankAccountFrom = findBankAccountByAccountNumber(assignment.getAccountNumberFrom());
        Operation operation = findOrCreateOperationById(assignment.getOperationId(), assignment);

        operationWebSocketPublisher.publishOperationCreated(operation);

        saveOperationWithStatus(operation, assignment.getStatus());

        if (assignment.getStatus() == OperationStatus.REJECTED) {
            return TransactionFinishStatus.TRANSACTION_REJECTED;
        }

        if (assignment.getTransferAccountType() == TransferAccountType.BANK_ACCOUNT) {
            return makeTransferToBankAccount(assignment, bankAccountFrom, operation);
        }

        return makeTransferToCreditAccount(assignment, bankAccountFrom, operation);
    }

    private TransactionFinishStatus makeTransferToBankAccount(TransferAssignmentMessage assignment, BankAccount bankAccountFrom, Operation operation) {
        BankAccount bankAccountTo = findBankAccountByAccountNumber(assignment.getAccountNumberTo());

        if (bankAccountFrom.isCurrentlyTransactional() || bankAccountTo.isCurrentlyTransactional()) {
            return TransactionFinishStatus.TRANSACTION_PAUSED;
        }

        if (bankAccountTo.isBanned()) {
            return finishRejectedOperation(operation);
        }

        BigDecimal amountFrom = assignment.getAmountFrom();
        BigDecimal amountTo = assignment.getAmountTo();

        if (bankAccountFrom.getBalance().compareTo(amountFrom) < 0) {
            return finishRejectedOperation(operation);
        }

        setCurrentlyTransactional(bankAccountFrom, bankAccountTo, null, true);

        bankAccountFrom.setBalance(bankAccountFrom.getBalance().subtract(amountFrom));
        bankAccountTo.setBalance(bankAccountTo.getBalance().add(amountTo));
        bankAccountRepository.save(bankAccountFrom);
        bankAccountRepository.save(bankAccountTo);

        sendMoneyUpdateMessages(bankAccountFrom, bankAccountTo);

        saveOperationWithStatus(operation, OperationStatus.SUCCESS);

        setCurrentlyTransactional(bankAccountFrom, bankAccountTo, null, false);

        return TransactionFinishStatus.TRANSACTION_FINISHED;
    }

    private TransactionFinishStatus makeTransferToCreditAccount(TransferAssignmentMessage assignment, BankAccount bankAccountFrom, Operation operation) {
        CreditAccount creditAccountTo = findCreditAccountByAccountNumber(assignment.getAccountNumberTo());

        if (bankAccountFrom.isCurrentlyTransactional() || creditAccountTo.isCurrentlyTransactional()) {
            return TransactionFinishStatus.TRANSACTION_PAUSED;
        }

        if (!creditAccountTo.isActive()) {
            return finishRejectedOperation(operation);
        }

        BigDecimal amount = assignment.getAmountFrom();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return finishRejectedOperation(operation);
        }

        setCurrentlyTransactional(bankAccountFrom, null, creditAccountTo, true);

        if (creditAccountTo.getDept().compareTo(amount) < 0) {
            amount = creditAccountTo.getDept();
            operation.setAmountFrom(amount);
        }

        BankAccount masterBankAccount = findBankAccountByAccountNumber(masterBankAccountNumber);

        bankAccountFrom.setBalance(bankAccountFrom.getBalance().subtract(amount));
        creditAccountTo.setDept(creditAccountTo.getDept().subtract(amount));
        masterBankAccount.setBalance(masterBankAccount.getBalance().add(amount));

        if (creditAccountTo.getDept().compareTo(BigDecimal.ZERO) == 0) {
            creditAccountTo.setClosed(true);

            operationHistoryService.createAndSaveOperationAboutAccountCornerOperation(creditAccountTo, AccountActionType.CLOSE_ACCOUNT);
        }

        bankAccountRepository.save(bankAccountFrom);
        creditAccountRepository.save(creditAccountTo);

        sendMoneyUpdateMessages(bankAccountFrom, creditAccountTo);

        saveOperationWithStatus(operation, OperationStatus.SUCCESS);

        updateCreditOperations(creditAccountTo, amount);

        setCurrentlyTransactional(bankAccountFrom, null, creditAccountTo, false);

        return TransactionFinishStatus.TRANSACTION_FINISHED;
    }

    private void updateCreditOperations(CreditAccount creditAccount, BigDecimal amount) {
        var notClosedOperations = operationRepository.findByAccountNumberFromAndDeptLeftGreaterThanAndTransferAccountTypeAndActionType(
                        creditAccount.getAccountNumber(),
                        BigDecimal.ZERO,
                        TransferAccountType.CREDIT_ACCOUNT,
                        AccountActionType.CREDIT_DEPT_PERCENT
                ).stream()
                .sorted(Comparator.comparing(Operation::getCreateTime))
                .toList();

        BigDecimal amountLeft = amount;
        Instant now = Instant.now();

        for (Operation operation : notClosedOperations) {
            if (amountLeft.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }

            BigDecimal operationDeptLeft = operation.getDeptLeft();

            if (operationDeptLeft.compareTo(amountLeft) <= 0) {
                amountLeft = amountLeft.subtract(operationDeptLeft);

                operation.setDeptLeft(BigDecimal.ZERO);
                operation.setOperationResolveTime(now);

                if (operation.getExpectedPaymentDate().isAfter(Instant.now())) {
                    operation.setPurchased(true);
                }
                
            } else {
                operation.setDeptLeft(operationDeptLeft.subtract(amountLeft));
                amountLeft = BigDecimal.ZERO;
            }

            operationRepository.save(operation);

            operationWebSocketPublisher.publishOperationUpdated(operation);
        }
    }

    private TransactionFinishStatus finishRejectedOperation(Operation operation) {
        saveOperationWithStatus(operation, OperationStatus.REJECTED);
        return TransactionFinishStatus.TRANSACTION_REJECTED;
    }

    private void saveOperationWithStatus(Operation operation, OperationStatus status) {
        operation.setStatus(status);
        operation.setUpdateTime(Instant.now());
        operationRepository.save(operation);

        operationWebSocketPublisher.publishOperationUpdated(operation);
    }

    private void sendMoneyUpdateMessages(BankAccount bankAccountFrom, BankAccount bankAccountTo) {
        operationWebSocketPublisher.publishAccountMoneyReceiving(bankAccountFrom);
        operationWebSocketPublisher.publishAccountMoneyReceiving(bankAccountTo);
    }

    private void sendMoneyUpdateMessages(BankAccount bankAccountFrom, CreditAccount creditAccountTo) {
        operationWebSocketPublisher.publishAccountMoneyReceiving(bankAccountFrom);
        operationWebSocketPublisher.publishAccountMoneyReceiving(creditAccountTo);
    }

    private void setCurrentlyTransactional(BankAccount bankAccountFrom, BankAccount bankAccountTo,
                                           CreditAccount creditAccountTo, boolean currentlyTransactional) {
        bankAccountFrom.setCurrentlyTransactional(currentlyTransactional);
        bankAccountRepository.save(bankAccountFrom);

        if (bankAccountTo != null) {
            bankAccountTo.setCurrentlyTransactional(currentlyTransactional);
            bankAccountRepository.save(bankAccountTo);
        }

        if (creditAccountTo != null) {
            creditAccountTo.setCurrentlyTransactional(currentlyTransactional);
            creditAccountRepository.save(creditAccountTo);
        }
    }

    private BankAccount findBankAccountByAccountNumber(String accountNumber) {
        return bankAccountRepository.getBankAccountByAccountNumberAndActiveTrue(accountNumber)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.ACCOUNT_NOT_FOUND));
    }

    private CreditAccount findCreditAccountByAccountNumber(String accountNumber) {
        return creditAccountRepository.findCreditAccountByAccountNumberAndActiveTrueAndClosedFalse(accountNumber)
                .orElseThrow(() -> new BadRequestException(ErrorMessages.CREDIT_NOT_FOUND));
    }

    private Operation findOrCreateOperationById(UUID operationId, TransferAssignmentMessage assignmentMessage) {
        var operation = operationRepository.getOperationByOperationId(operationId);

        return operation.orElseGet(() -> operationHistoryService.createAndSaveOperation(assignmentMessage));
    }
}
