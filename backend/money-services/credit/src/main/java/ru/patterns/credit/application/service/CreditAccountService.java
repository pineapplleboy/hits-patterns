package ru.patterns.credit.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.kafka.provider.CreditProvider;
import ru.patterns.credit.domain.entity.CreditRate;
import ru.patterns.credit.domain.repository.CreditRateRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.exception.ForbiddenException;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.kafka.TakeCreditMessage;
import ru.patterns.shared.model.log.TracingLog;
import ru.patterns.shared.model.response.OperationStatusResponseModel;
import ru.patterns.shared.monitoring.logger.MonitoringLogger;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditAccountService {

    private final CreditRatingService creditRatingService;
    private final CreditProvider creditProvider;
    private final CreditRateRepository creditRateRepository;
    private final MonitoringLogger monitoringLogger;

    private final static int MIN_ALLOWED_CREDIT_RATING = 235;
    private final static BigDecimal MAX_CREDIT_AMOUNT = BigDecimal.valueOf(500000);

    public OperationStatusResponseModel takeCredit(UUID userId, UUID rateId, BigDecimal sum, String bankAccountNum, String token, TracingLog logData) {
        monitoringLogger.logInfo(logData, "Получен запрос на оформление кредита");

        if (MAX_CREDIT_AMOUNT.compareTo(sum) <= 0) {
            monitoringLogger.logWarn(logData, "Попытка оформить кредит на слишком большую сумму");
            throw new BadRequestException(ErrorMessages.INVALID_CREDIT_SUM, logData);
        }

        var userCreditRating = creditRatingService.getUserCreditRating(userId, token, logData);

        if (userCreditRating.getRating() < MIN_ALLOWED_CREDIT_RATING) {
            monitoringLogger.logWarn(logData, "Попытка оформить кредит с недостаточным кредитным рейтингом");
            throw new ForbiddenException(ErrorMessages.CREDIT_RATING_TOO_LOW, logData);
        }

        CreditRate creditRate = creditRateRepository.findById(rateId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CREDIT_RATE_NOT_FOUND, logData));

        creditProvider.send(createTakeCreditMessage(userId, creditRate, sum, bankAccountNum), token);

        monitoringLogger.logInfo(logData, "Заявка на оформление кредита успешно отправлена в обработку");

        return new OperationStatusResponseModel(OperationStatus.SUCCESS);
    }

    private TakeCreditMessage createTakeCreditMessage(UUID userId, CreditRate creditRate, BigDecimal sum, String bankAccountNum) {
        return new TakeCreditMessage()
                .setUserId(userId)
                .setCreditRateName(creditRate.getName())
                .setCreditRatePercent(creditRate.getPercent())
                .setWriteOffPeriod(creditRate.getWriteOffPeriod())
                .setCreditAmount(sum)
                .setBankAccountNumber(bankAccountNum);
    }
}
