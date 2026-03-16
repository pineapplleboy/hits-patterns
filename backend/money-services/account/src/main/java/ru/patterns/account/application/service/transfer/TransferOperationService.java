package ru.patterns.account.application.service.transfer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.common.enums.TransactionFinishStatus;
import ru.patterns.account.application.service.operation.OperationHistoryService;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.entity.CreditAccount;
import ru.patterns.account.domain.entity.Operation;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.account.domain.repository.CreditAccountRepository;
import ru.patterns.account.domain.repository.OperationRepository;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.shared.model.kafka.TransferAssignmentMessage;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransferOperationService {

    private final BankAccountRepository bankAccountRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final OperationRepository operationRepository;
    private final OperationHistoryService operationHistoryService;

    @Value("${master-bank.account-number}")
    private String masterBankAccountNumber;

    @Transactional
    public TransactionFinishStatus makeTransfer(TransferAssignmentMessage assignment) {
        BankAccount bankAccountFrom = findBankAccountByAccountNumber(assignment.getAccountNumberFrom());
        Operation operation = findOperationById(assignment.getOperationId());

        operation.setStatus(OperationStatus.IN_PROCESS);
        operationRepository.save(operation);

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

        BigDecimal amount = assignment.getAmount();

        if (bankAccountFrom.getBalance().compareTo(amount) < 0) {
            return finishRejectedOperation(operation);
        }

        setCurrentlyTransactional(bankAccountFrom, bankAccountTo, null, true);

        bankAccountFrom.setBalance(bankAccountFrom.getBalance().subtract(amount));
        bankAccountTo.setBalance(bankAccountTo.getBalance().add(amount));
        bankAccountRepository.save(bankAccountFrom);
        bankAccountRepository.save(bankAccountTo);

        operation.setStatus(OperationStatus.SUCCESS);
        operationRepository.save(operation);

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

        BigDecimal amount = assignment.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return finishRejectedOperation(operation);
        }

        setCurrentlyTransactional(bankAccountFrom, null, creditAccountTo, true);

        if (creditAccountTo.getDept().compareTo(amount) < 0) {
            amount = creditAccountTo.getDept();
            operation.setAmount(amount);
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

        operation.setStatus(OperationStatus.SUCCESS);
        operationRepository.save(operation);

        setCurrentlyTransactional(bankAccountFrom, null, creditAccountTo, false);

        return TransactionFinishStatus.TRANSACTION_FINISHED;
    }

    private TransactionFinishStatus finishRejectedOperation(Operation operation) {
        operation.setStatus(OperationStatus.REJECTED);
        operationRepository.save(operation);
        return TransactionFinishStatus.TRANSACTION_REJECTED;
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
                .orElseThrow(() -> new BadRequestException("Bank account not found"));
    }

    private CreditAccount findCreditAccountByAccountNumber(String accountNumber) {
        return creditAccountRepository.findCreditAccountByAccountNumberAndActiveTrueAndClosedFalse(accountNumber)
                .orElseThrow(() -> new BadRequestException("Credit account not found"));
    }

    private Operation findOperationById(UUID operationId) {
        return operationRepository.getOperationByOperationId(operationId)
                .orElseThrow(() -> new BadRequestException("Operation not found"));
    }
}
