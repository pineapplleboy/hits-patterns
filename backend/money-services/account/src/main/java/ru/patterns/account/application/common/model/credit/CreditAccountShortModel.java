package ru.patterns.account.application.common.model.credit;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.shared.infrastructure.DurationSerializer;

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

    @JsonSerialize(using = DurationSerializer.class)
    private Duration writeOffPeriod;

    private Instant nextWriteOffDate;
}
