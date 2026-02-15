package ru.patterns.credit.domain.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;

@Getter
public class CreditRateCreateModel {
    @NotNull
    @Size(min = 1, max = 100, message = "Длина наименование процентной ставки должна содержать от 1 до 100 символов")
    private String name;
    @Min(value = 0, message = "Минимальный допустимый процент - 0%")
    @Max(value = 100, message = "Максимальный допустимый процент - 100%")
    private int percent;
    @DurationMin(minutes = 1, message = "Минимальный период списания - 1 минута")
    private Duration writeOffPeriod;
}
