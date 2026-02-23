package ru.patterns.credit.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.credit.application.kafka.CreditProducer;
import ru.patterns.credit.domain.entity.CreditRate;
import ru.patterns.credit.domain.repository.CreditRateRepository;
import ru.patterns.shared.exception.NotFoundException;
import ru.patterns.shared.model.enums.OperationStatus;
import ru.patterns.shared.model.kafka.TakeCreditMessage;
import ru.patterns.shared.model.response.OperationStatusResponseModel;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditAccountService {

    private final CreditProducer creditProducer;
    private final CreditRateRepository creditRateRepository;

    public OperationStatusResponseModel takeCredit(UUID userId, UUID rateId) {
        CreditRate creditRate = creditRateRepository.findById(rateId)
                .orElseThrow(() -> new NotFoundException("Credit rate not found"));

        creditProducer.send(createTakeCreditMessage(userId, creditRate));

        return new OperationStatusResponseModel(OperationStatus.SUCCESS);
    }

    private TakeCreditMessage createTakeCreditMessage(UUID userId, CreditRate creditRate) {
        return new TakeCreditMessage()
                .setUserId(userId)
                .setCreditRateName(creditRate.getName())
                .setCreditRatePercent(creditRate.getPercent())
                .setWriteOffPeriod(creditRate.getWriteOffPeriod());
    }
}
