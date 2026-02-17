package ru.patterns.credit.domain.mapper;

import ru.patterns.credit.application.common.model.response.CreditRateModel;
import ru.patterns.credit.domain.entity.CreditRate;

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
