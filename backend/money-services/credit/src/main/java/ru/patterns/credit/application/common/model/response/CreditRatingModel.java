package ru.patterns.credit.application.common.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class CreditRatingModel {

    private long rating;

    private long totalCreditCounter;

    private long closedCreditCounter;

    private long activeCreditAmount;

    private long expiredOperationsAmount;
}
