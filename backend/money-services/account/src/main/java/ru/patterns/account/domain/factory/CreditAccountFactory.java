package ru.patterns.account.domain.factory;

import lombok.experimental.UtilityClass;
import ru.patterns.account.application.utility.CreditUtility;
import ru.patterns.account.domain.entity.CreditAccount;
import ru.patterns.shared.model.kafka.TakeCreditMessage;

@UtilityClass
public class CreditAccountFactory {

    public CreditAccount createCreditAccount(TakeCreditMessage takeCreditMessage) {
        return new CreditAccount().
                setUserId(takeCreditMessage.getUserId())
                .setAccountNumber(BankAccountFactory.generateAccountNumber())
                .setDept(takeCreditMessage.getCreditAmount())
                .setCreditRateName(takeCreditMessage.getCreditRateName())
                .setCreditRatePercent(takeCreditMessage.getCreditRatePercent())
                .setWriteOffPeriod(takeCreditMessage.getWriteOffPeriod())
                .setNextWriteOffDate(CreditUtility.calculateNextCreditWriteOffDate(takeCreditMessage.getWriteOffPeriod()))
                .setActive(true)
                .setClosed(false);
    }
}
