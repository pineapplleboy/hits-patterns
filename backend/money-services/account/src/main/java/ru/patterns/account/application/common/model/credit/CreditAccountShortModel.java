package ru.patterns.account.application.common.model.credit;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class CreditAccountShortModel {

    private UUID id;

    private String accountNumber;

    private BigDecimal dept;

    private String creditRateName;

    private int creditRatePercent;

    private Duration writeOffPeriod;

    private Instant nextWriteOffDate;
}
