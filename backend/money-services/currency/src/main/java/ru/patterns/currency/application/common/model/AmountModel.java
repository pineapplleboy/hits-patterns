package ru.patterns.currency.application.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AmountModel {

    private ProcessedCurrencyModel fromCurrency;

    private ProcessedCurrencyModel toCurrency;

    private BigDecimal amount;
}
