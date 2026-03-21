package ru.patterns.credit.application.common.model.account;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.experimental.Accessors;
import ru.patterns.credit.application.common.model.operation.CreditOperationModel;
import ru.patterns.credit.application.infrastructure.DurationDeserializer;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class CreditAccountHistoryModel {

    private UUID id;

    private String accountNumber;

    private String dept;

    private String creditRateName;

    private int creditRatePercent;

    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration writeOffPeriod;

    private Instant nextWriteOffDate;

    private boolean banned = false;

    private List<CreditOperationModel> operations;
}
