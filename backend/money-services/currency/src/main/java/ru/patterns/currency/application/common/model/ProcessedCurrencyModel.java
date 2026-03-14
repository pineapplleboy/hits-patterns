package ru.patterns.currency.application.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class ProcessedCurrencyModel {

    private Integer id;

    private String name;

    private String charCode;
}
