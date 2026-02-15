package ru.patterns.credit.domain.model.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.patterns.credit.infrastructure.DurationSerializer;

import java.time.Duration;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreditRateModel {
    private UUID rateId;
    private String name;
    private int percent;
    @JsonSerialize(using = DurationSerializer.class)
    private Duration writeOffPeriod;
}
