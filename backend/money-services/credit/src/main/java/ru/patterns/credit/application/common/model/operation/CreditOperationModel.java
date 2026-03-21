package ru.patterns.credit.application.common.model.operation;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Accessors(chain = true)
public class CreditOperationModel extends OperationModel {

    private Instant expectedPaymentDate = Instant.now();

    private BigDecimal deptLeft = BigDecimal.ZERO;

    private boolean expired = false;
}
