package ru.patterns.account.domain.mapper;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.common.model.credit.CreditAccountFullModel;
import ru.patterns.account.application.common.model.credit.CreditAccountShortModel;
import ru.patterns.account.domain.entity.CreditAccount;

@UtilityClass
public class CreditAccountMapper {

    public CreditAccountShortModel toModel(CreditAccount creditAccount) {
        return new CreditAccountShortModel()
                .setId(creditAccount.getId())
                .setAccountNumber(creditAccount.getAccountNumber())
                .setCreditRateName(creditAccount.getCreditRateName())
                .setDept(creditAccount.getDept())
                .setCreditRatePercent(creditAccount.getCreditRatePercent())
                .setNextWriteOffDate(creditAccount.getNextWriteOffDate())
                .setWriteOffPeriod(creditAccount.getWriteOffPeriod());
    }

    public CreditAccountFullModel toFullModel(CreditAccount creditAccount) {
        return new CreditAccountFullModel()
                .setId(creditAccount.getId())
                .setAccountNumber(creditAccount.getAccountNumber())
                .setCreditRateName(creditAccount.getCreditRateName())
                .setDept(creditAccount.getDept())
                .setCreditRatePercent(creditAccount.getCreditRatePercent())
                .setNextWriteOffDate(creditAccount.getNextWriteOffDate())
                .setWriteOffPeriod(creditAccount.getWriteOffPeriod());
    }
}
