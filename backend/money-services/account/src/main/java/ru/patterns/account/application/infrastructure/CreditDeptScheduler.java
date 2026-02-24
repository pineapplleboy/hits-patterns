package ru.patterns.account.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.service.CreditRateService;

@Component
@RequiredArgsConstructor
public class CreditDeptScheduler {

    private final CreditRateService creditRateService;

    @Scheduled(fixedRate = 60000)
    public void performCreditDeptCalculation() {
        creditRateService.updateCreditAccounts();
    }
}
