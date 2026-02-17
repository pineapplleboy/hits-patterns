package ru.patterns.credit.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class CreditRate {
    @Id
    private UUID rateId = UUID.randomUUID();
    @NotNull
    private String name;
    private int percent = 10; // значение от 0 до 100 включительно
    private Duration writeOffPeriod = Duration.ofDays(1);
    private boolean isActive = true;
    private Instant createTime = Instant.now();
    private Instant updateTime = Instant.now();

    public CreditRate(String name, int percent, Duration writeOffPeriod) {
        this.name = name;
        this.percent = percent;
        this.writeOffPeriod = writeOffPeriod;
    }
}
