package ru.patterns.credit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.domain.model.request.CreditRateCreateModel;
import ru.patterns.credit.domain.model.response.CreditRateModel;
import ru.patterns.credit.domain.model.response.UuidResponseModel;
import ru.patterns.credit.entity.CreditRate;
import ru.patterns.credit.mapper.CreditRateMapper;
import ru.patterns.credit.repository.CreditRateRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditRateService {
    private final CreditRateRepository creditRateRepository;

    public List<CreditRateModel> getCreditRates() {
        return creditRateRepository.findByIsActiveTrue().stream()
                .map(CreditRateMapper::toModel)
                .toList();
    }

    public UuidResponseModel createCreditRate(CreditRateCreateModel request) {
        CreditRate newCreditRate = new CreditRate(request.getName(), request.getPercent(), request.getWriteOffPeriod());
        creditRateRepository.save(newCreditRate);

        return new UuidResponseModel(newCreditRate.getRateId());
    }
}
