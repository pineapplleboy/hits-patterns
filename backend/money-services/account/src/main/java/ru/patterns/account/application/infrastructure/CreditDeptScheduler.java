package ru.patterns.account.application.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.service.account.CreditDeptService;

@Component
@RequiredArgsConstructor
public class CreditDeptScheduler {

    private final CreditDeptService creditDeptService;

    @Scheduled(fixedRate = 60000)
    public void performCreditDeptCalculation() {
        creditDeptService.updateCreditAccounts();
    }
}
