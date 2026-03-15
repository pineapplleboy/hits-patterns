package ru.patterns.account.application.common.model.currency;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyModel {

    private Integer id;

    private String name;

    private String charCode;

    private String symbol;

    private BigDecimal rate;

    public CurrencyModel(Integer id) {
        this.id = id;
    }
}
