package ru.patterns.currency.application.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain=true)
public class AmountModel {

    private ProcessedCurrencyModel fromCurrency;

    private ProcessedCurrencyModel toCurrency;

    private BigDecimal amount;

    private BigDecimal amountFinal;
}
