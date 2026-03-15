package ru.patterns.account.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.model.bankaccount.BankAccountFullModel;
import ru.patterns.account.application.common.model.bankaccount.BankAccountShortModel;
import ru.patterns.account.application.utility.CurrencySymbolUtility;
import ru.patterns.account.domain.entity.BankAccount;

@UtilityClass
public class BankAccountMapper {

    public BankAccountShortModel toShortModel(BankAccount bankAccount, boolean hidden) {
        return new BankAccountShortModel()
                .setAccountNumber(bankAccount.getAccountNumber())
                .setId(bankAccount.getId())
                .setBalance(bankAccount.getBalance().toString() + CurrencySymbolUtility.getCurrencySymbol(bankAccount.getCurrencyId()))
                .setBanned(bankAccount.isBanned())
                .setHidden(hidden);
    }

    public BankAccountFullModel toFullModelWithoutComments(BankAccount bankAccount) {
        return new BankAccountFullModel()
                .setId(bankAccount.getId())
                .setAccountNumber(bankAccount.getAccountNumber())
                .setBalance(bankAccount.getBalance())
                .setCreateTime(bankAccount.getCreateTime())
                .setBanned(bankAccount.isBanned());
    }
}
