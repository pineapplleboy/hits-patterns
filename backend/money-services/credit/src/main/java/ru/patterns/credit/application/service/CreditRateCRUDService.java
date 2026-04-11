package ru.patterns.credit.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.common.constants.ErrorMessages;
import ru.patterns.credit.application.common.model.request.CreditRateDataModel;
import ru.patterns.credit.application.common.model.response.CreditRateModel;
import ru.patterns.credit.domain.entity.CreditRate;
import ru.patterns.credit.domain.mapper.CreditRateMapper;
import ru.patterns.credit.domain.repository.CreditRateRepository;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.response.UuidResponseModel;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditRateCRUDService {

    private final CreditRateRepository creditRateRepository;
    private final MonitoringLogger monitoringLogger;

    public List<CreditRateModel> getCreditRates(TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение списка кредитных тарифов");

        return creditRateRepository.findByIsActiveTrue().stream()
                .map(CreditRateMapper::toModel)
                .toList();
    }

    public CreditRateModel getCreditRateById(UUID id, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на получение кредитного тарифа");

        return creditRateRepository.findById(id)
                .map(CreditRateMapper::toModel)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CREDIT_RATE_NOT_FOUND + id, logData));
    }

    public UuidResponseModel createCreditRate(CreditRateDataModel request, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на создание кредитного тарифа");

        Optional<CreditRate> activeCreditRate = creditRateRepository.findByNameAndIsActiveTrue(request.getName());

        if (activeCreditRate.isPresent()) {
            monitoringLogger.logWarn(logData, "Попытка создать кредитный тариф с уже существующим названием");
            throw new BadRequestException(ErrorMessages.CREDIT_RATE_WITH_THAT_NAME_ALREADY_EXISTS, logData);
        }

        CreditRate newCreditRate = new CreditRate(request.getName(), request.getPercent(), request.getWriteOffPeriod());
        creditRateRepository.save(newCreditRate);

        return new UuidResponseModel(newCreditRate.getRateId());
    }

    public void updateCreditRateModel(UUID id, CreditRateDataModel creditRateDataModel, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на обновление кредитного тарифа");

        CreditRate creditRate = findCreditByIdOrThrowException(id, logData);

        creditRate.setName(creditRateDataModel.getName());
        creditRate.setPercent(creditRateDataModel.getPercent());
        creditRate.setWriteOffPeriod(creditRateDataModel.getWriteOffPeriod());
        creditRate.setUpdateTime(Instant.now());

        creditRateRepository.save(creditRate);
    }

    public void deactivateCreditRateById(UUID id, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на удаление кредитного тарифа");

        CreditRate creditRate = findCreditByIdOrThrowException(id, logData);
        creditRate.setActive(false);
        creditRateRepository.save(creditRate);
    }

    private CreditRate findCreditByIdOrThrowException(UUID id, TracingLog logData) {
        return creditRateRepository.findByRateIdAndIsActiveTrue(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CREDIT_RATE_NOT_FOUND + id, logData));
    }
}
