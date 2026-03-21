package ru.patterns.credit.application.common.model.operation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain=true)
@AllArgsConstructor
@NoArgsConstructor
public class CreditStats {

    private long totalCreditCounter = 0;

    private long closedCreditCounter = 0;

    private long activeCreditAmount = 0;

    private long expiredCreditAmount = 0;

    private BigDecimal totalCurrentDebt = BigDecimal.ZERO;
}
