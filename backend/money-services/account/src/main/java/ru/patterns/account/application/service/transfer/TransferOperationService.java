package ru.patterns.account.application.service.transfer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.model.request.MoneyAmountRequestModel;
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
public class TransferOperationService {

    private final BankAccountRepository bankAccountRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final OperationRepository operationRepository;

    public void validateAccountRemainder(String accountNumber, MoneyAmountRequestModel requestModel) {
        BankAccount bankAccount = bankAccountRepository
                .getBankAccountByAccountNumberAndActiveTrue(accountNumber)
                .orElseThrow(() -> new BadRequestException("Account number not found"));

        if (bankAccount.getBalance().compareTo(requestModel.getAmount()) < 0) {
            throw new BadRequestException("Incorrect request amount");
        }
    }

    public void makeTransfer(TransferAssignmentMessage assignment) {
        BankAccount bankAccountFrom = findBankAccountByAccountNumber(assignment.getAccountNumberFrom());
        Operation operation = findOperationById(assignment.getOperationId());

        operation.setStatus(OperationStatus.IN_PROCESS);
        operationRepository.save(operation);

        if (assignment.getTransferAccountType() == TransferAccountType.BANK_ACCOUNT) {
            makeTransferToBankAccount(assignment, bankAccountFrom, operation);
            return;
        }

        makeTransferToCreditAccount(assignment, bankAccountFrom, operation);
    }

    private void makeTransferToBankAccount(TransferAssignmentMessage assignment, BankAccount bankAccountFrom, Operation operation) {
        BankAccount bankAccountTo = findBankAccountByAccountNumber(assignment.getAccountNumberTo());

        BigDecimal amount = assignment.getAmount();

        bankAccountFrom.setBalance(bankAccountFrom.getBalance().subtract(amount));
        bankAccountTo.setBalance(bankAccountTo.getBalance().add(amount));

        bankAccountRepository.save(bankAccountFrom);
        bankAccountRepository.save(bankAccountTo);

        operation.setStatus(OperationStatus.SUCCESS);
        operationRepository.save(operation);
    }

    private void makeTransferToCreditAccount(TransferAssignmentMessage assignment, BankAccount bankAccountFrom, Operation operation) {
        CreditAccount creditAccountTo = findCreditAccountByAccountNumber(assignment.getAccountNumberFrom());

        BigDecimal amount = assignment.getAmount();

        if (creditAccountTo.getDept().compareTo(amount) < 0) {
            operation.setStatus(OperationStatus.REJECTED);
            operationRepository.save(operation);
            return;
        }

        bankAccountFrom.setBalance(bankAccountFrom.getBalance().subtract(amount));
        creditAccountTo.setDept(creditAccountTo.getDept().subtract(amount));

        if (creditAccountTo.getDept().compareTo(BigDecimal.ZERO) == 0) {
            creditAccountTo.setClosed(true);
        }

        bankAccountRepository.save(bankAccountFrom);
        creditAccountRepository.save(creditAccountTo);

        operation.setStatus(OperationStatus.SUCCESS);
        operationRepository.save(operation);
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
