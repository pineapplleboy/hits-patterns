package ru.patterns.credit.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.kafka.provider.CreditProvider;
import ru.patterns.credit.domain.entity.CreditRate;
import ru.patterns.credit.domain.repository.CreditRateRepository;
import ru.patterns.shared.constants.ErrorMessages;
import ru.patterns.shared.exception.BadRequestException;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.kafka.TakeCreditMessage;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditAccountService {

    private final CreditProvider creditProvider;
    private final CreditRateRepository creditRateRepository;

    private final BigDecimal maxCreditAmount = BigDecimal.valueOf(500000);

    public OperationStatusResponseModel takeCredit(UUID userId, UUID rateId, BigDecimal sum, String bankAccountNum, String token) {
        if (maxCreditAmount.compareTo(sum) <= 0) {
            throw new BadRequestException(ErrorMessages.INVALID_CREDIT_SUM);
        }

        CreditRate creditRate = creditRateRepository.findById(rateId)
                .orElseThrow(() -> new NotFoundException(ErrorMessages.CREDIT_RATE_NOT_FOUND));

        creditProvider.send(createTakeCreditMessage(userId, creditRate, sum, bankAccountNum), token);

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
