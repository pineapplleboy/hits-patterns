package ru.patterns.shared.model.client;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain=true)
public class CurrencyAmountModel {

    private ProcessedCurrencyModel fromCurrency;

    private ProcessedCurrencyModel toCurrency;

    private BigDecimal amount;

    private BigDecimal amountFinal;
}
