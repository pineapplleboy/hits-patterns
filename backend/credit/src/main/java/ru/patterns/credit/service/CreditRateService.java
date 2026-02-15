package ru.patterns.credit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.domain.constants.ErrorMessages;
import ru.patterns.credit.domain.exception.NotFoundException;
import ru.patterns.credit.domain.model.request.CreditRateDataModel;
import ru.patterns.credit.domain.model.response.CreditRateModel;
import ru.patterns.credit.domain.model.response.UuidResponseModel;
import ru.patterns.credit.entity.CreditRate;
import ru.patterns.credit.mapper.CreditRateMapper;
import ru.patterns.credit.repository.CreditRateRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditRateService {
    private final CreditRateRepository creditRateRepository;

    public List<CreditRateModel> getCreditRates() {
        return creditRateRepository.findByIsActiveTrue().stream()
                .map(CreditRateMapper::toModel)
                .toList();
    }

    public CreditRateModel getCreditRateById(UUID id) {
        return creditRateRepository.findById(id)
                .map(CreditRateMapper::toModel)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CREDIT_RATE_NOT_FOUND + id));
    }

    public UuidResponseModel createCreditRate(CreditRateDataModel request) {
        CreditRate newCreditRate = new CreditRate(request.getName(), request.getPercent(), request.getWriteOffPeriod());
        creditRateRepository.save(newCreditRate);

        return new UuidResponseModel(newCreditRate.getRateId());
    }

    public void updateCreditRateModel(UUID id, CreditRateDataModel creditRateDataModel) {
        CreditRate creditRate = findCreditByIdOrThrowException(id);

        creditRate.setName(creditRateDataModel.getName());
        creditRate.setPercent(creditRateDataModel.getPercent());
        creditRate.setWriteOffPeriod(creditRateDataModel.getWriteOffPeriod());

        creditRateRepository.save(creditRate);
    }

    public void deleteCreditRateById(UUID id) {
        creditRateRepository.delete(findCreditByIdOrThrowException(id));
    }

    private CreditRate findCreditByIdOrThrowException(UUID id) {
        return creditRateRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CREDIT_RATE_NOT_FOUND + id));
    }
}
