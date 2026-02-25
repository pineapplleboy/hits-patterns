package ru.patterns.account.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.model.bankaccount.BankAccountFullModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountShortModel;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.shared.utility.MaskUtility;

@UtilityClass
public class BankAccountMapper {

    public BankAccountShortModel toShortModel(BankAccount bankAccount) {
        return new BankAccountShortModel()
                .setAccountNumber(MaskUtility.maskAccountNumber(bankAccount.getAccountNumber()))
                .setId(bankAccount.getId())
                .setBalance(bankAccount.getBalance());
    }

    public BankAccountFullModel toFullModelWithoutComments(BankAccount bankAccount) {
        return new BankAccountFullModel()
                .setId(bankAccount.getId())
                .setAccountNumber(MaskUtility.maskAccountNumber(bankAccount.getAccountNumber()))
                .setBalance(bankAccount.getBalance())
                .setCreateTime(bankAccount.getCreateTime());
    }
}
