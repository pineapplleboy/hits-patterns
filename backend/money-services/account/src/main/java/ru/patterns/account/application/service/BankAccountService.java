package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.TransferAccountType;
import ru.patterns.account.application.common.model.AccountNumberResponseModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountFullModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountShortModel;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.factory.BankAccountFactory;
import ru.patterns.account.domain.mapper.BankAccountMapper;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.ForbiddenException;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.external.AuthUser;
import ru.patterns.shared.model.external.Role;
import ru.patterns.shared.utility.AuthUtility;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ExtensionMethod(BankAccountMapper.class)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final OperationService operationService;

    public AccountNumberResponseModel createBankAccount(AuthUser authUser, UUID userId) {
        if (!authUser.userId().equals(userId)) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN);
        }

        BankAccount bankAccount = BankAccountFactory.createBankAccount(userId);
        bankAccountRepository.save(bankAccount);

        return new AccountNumberResponseModel(bankAccount.getAccountNumber());
    }

    public List<BankAccountShortModel> getAllBankAccounts(AuthUser authUser) {
        if (authUser.role() != Role.EMPLOYEE) {
            throw new ForbiddenException(ErrorMessages.FORBIDDEN);
        }

        return bankAccountRepository.findAll()
                .stream()
                .filter(BankAccount::isActive)
                .map(account -> account.toShortModel())
                .toList();
    }

    public List<BankAccountShortModel> getAllUserBankAccounts(AuthUser authUser, UUID userId) {
        AuthUtility.checkUserRights(authUser, userId);

        return bankAccountRepository.getBankAccountsByUserIdAndActive(userId, true)
                .stream()
                .map(account -> account.toShortModel())
                .toList();
    }

    public BankAccountFullModel getBankAccountFullModel(AuthUser authUser, UUID userId, String accountNumber) {
        AuthUtility.checkUserRights(authUser, userId);

        var account = bankAccountRepository.getBankAccountByAccountNumberAndActiveAndUserId(accountNumber, true, userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.ACCOUNT_NOT_FOUND));

        var accountFullModel = account.toFullModelWithoutComments();
        var operations = operationService.getAccountOperations(authUser, authUser.userId(), accountNumber, TransferAccountType.BANK_ACCOUNT);

        accountFullModel.setOperations(operations);

        return accountFullModel;
    }
}
