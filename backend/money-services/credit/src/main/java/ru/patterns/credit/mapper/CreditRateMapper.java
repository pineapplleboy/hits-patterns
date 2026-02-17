package ru.patterns.credit.mapper;

import ru.patterns.credit.domain.model.response.CreditRateModel;
import ru.patterns.credit.entity.CreditRate;

public class CreditRateMapper {

    public static CreditRateModel toModel(CreditRate entity) {
        return new CreditRateModel(
                entity.getRateId(),
                entity.getName(),
                entity.getPercent(),
                entity.getWriteOffPeriod()
        );
    }
}
