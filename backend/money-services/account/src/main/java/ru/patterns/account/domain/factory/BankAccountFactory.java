package ru.patterns.account.domain.factory;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import ru.patterns.account.application.common.enums.BankAccountType;
import ru.patterns.account.domain.entity.BankAccount;

import java.math.BigDecimal;
import java.util.UUID;

@UtilityClass
public class BankAccountFactory {

    public BankAccount createBankAccount(UUID userId, Integer currencyId) {
        return new BankAccount()
                .setUserId(userId)
                .setAccountNumber(generateAccountNumber())
                .setAccountType(BankAccountType.CLIENT)
                .setBalance(BigDecimal.valueOf(0))
                .setCurrencyId(currencyId);
    }

    public String generateAccountNumber() {
        return randomString() + "-" + randomString() + "-" + randomString() + "-" + randomString();
    }

    private String randomString() {
        return RandomStringUtils.randomNumeric(4);
    }
}
