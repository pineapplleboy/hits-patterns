package ru.patterns.credit.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreditRateModel {
    private UUID rateId;
    private String name;
    private int percent;
    private Duration writeOffPeriod;
}
