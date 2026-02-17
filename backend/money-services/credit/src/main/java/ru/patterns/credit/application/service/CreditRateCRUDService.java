package ru.patterns.credit.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.common.constants.ErrorMessages;
import ru.patterns.credit.application.common.exception.BadRequestException;
import ru.patterns.credit.application.common.exception.NotFoundException;
import ru.patterns.credit.application.common.model.request.CreditRateDataModel;
import ru.patterns.credit.application.common.model.response.CreditRateModel;
import ru.patterns.credit.application.common.model.response.UuidResponseModel;
import ru.patterns.credit.domain.entity.CreditRate;
import ru.patterns.credit.domain.mapper.CreditRateMapper;
import ru.patterns.credit.domain.repository.CreditRateRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditRateCRUDService {
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
        Optional<CreditRate> activeCreditRate = creditRateRepository.findByNameAndIsActiveTrue(request.getName());

        if (activeCreditRate.isPresent()) {
            throw new BadRequestException(ErrorMessages.CREDIT_RATE_WITH_THAT_NAME_ALREADY_EXISTS);
        }

        CreditRate newCreditRate = new CreditRate(request.getName(), request.getPercent(), request.getWriteOffPeriod());
        creditRateRepository.save(newCreditRate);

        return new UuidResponseModel(newCreditRate.getRateId());
    }

    public void updateCreditRateModel(UUID id, CreditRateDataModel creditRateDataModel) {
        CreditRate creditRate = findCreditByIdOrThrowException(id);

        creditRate.setName(creditRateDataModel.getName());
        creditRate.setPercent(creditRateDataModel.getPercent());
        creditRate.setWriteOffPeriod(creditRateDataModel.getWriteOffPeriod());
        creditRate.setUpdateTime(Instant.now());

        creditRateRepository.save(creditRate);
    }

    public void deactivateCreditRateById(UUID id) {
        CreditRate creditRate = findCreditByIdOrThrowException(id);

        creditRate.setActive(false);

        creditRateRepository.save(creditRate);
    }

    private CreditRate findCreditByIdOrThrowException(UUID id) {
        return creditRateRepository.findByRateIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CREDIT_RATE_NOT_FOUND + id));
    }
}
