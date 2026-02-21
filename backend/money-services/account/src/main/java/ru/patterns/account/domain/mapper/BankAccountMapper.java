package ru.patterns.account.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.model.BankAccountFullModel;
import ru.patterns.account.application.common.model.BankAccountShortModel;
import ru.patterns.account.domain.entity.BankAccount;

@UtilityClass
public class BankAccountMapper {
    public BankAccountShortModel toShortModel(BankAccount bankAccount) {
        return new BankAccountShortModel()
                .setAccountNumber(bankAccount.getAccountNumber())
                .setId(bankAccount.getId())
                .setBalance(bankAccount.getBalance());
    }

    public BankAccountFullModel toFullModelWithoutComments(BankAccount bankAccount) {
        return new BankAccountFullModel()
                .setId(bankAccount.getId())
                .setAccountNumber(bankAccount.getAccountNumber())
                .setBalance(bankAccount.getBalance())
                .setCreateTime(bankAccount.getCreateTime());
    }
}
