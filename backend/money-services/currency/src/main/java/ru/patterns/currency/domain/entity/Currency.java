package ru.patterns.currency.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Currency {

    @Id
    private Integer id;

    private String name;

    private String charCode;

    private String symbol;

    private BigDecimal rate = null;

    private boolean active = true;
}
