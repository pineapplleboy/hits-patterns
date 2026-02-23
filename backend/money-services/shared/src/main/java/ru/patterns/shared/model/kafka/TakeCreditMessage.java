package ru.patterns.shared.model.kafka;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.UUID;

@Data
@Accessors(chain=true)
public class TakeCreditMessage {

    private UUID applicationId = UUID.randomUUID();

    private UUID userId;

    private String creditRateName;

    private int creditRatePercent;

    private Duration writeOffPeriod;

    private BigDecimal creditAmount;
}
