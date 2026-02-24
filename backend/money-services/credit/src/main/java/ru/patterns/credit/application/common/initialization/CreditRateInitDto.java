package ru.patterns.credit.application.common.initialization;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import ru.patterns.credit.application.infrastructure.DurationDeserializer;

import java.time.Duration;

@Data
public class CreditRateInitDto {
    private String name;
    private int percent;
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration writeOffPeriod;
}
