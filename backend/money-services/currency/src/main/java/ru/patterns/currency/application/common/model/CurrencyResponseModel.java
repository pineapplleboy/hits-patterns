package ru.patterns.currency.application.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain=true)
public class CurrencyResponseModel {

    private Integer id;

    private String name;

    private String charCode;

    private String symbol;

    private BigDecimal rate;
}
