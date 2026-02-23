package ru.patterns.account.application.utility;

import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Instant;

@UtilityClass
public class CreditUtility {

    public Instant calculateNextCreditWriteOffDate(Instant previousWriteOffDate, Duration writeOffPeriod) {
        return previousWriteOffDate.plus(writeOffPeriod);
    }

    public Instant calculateNextCreditWriteOffDate(Duration writeOffPeriod) {
        return Instant.now().plus(writeOffPeriod);
    }
}
