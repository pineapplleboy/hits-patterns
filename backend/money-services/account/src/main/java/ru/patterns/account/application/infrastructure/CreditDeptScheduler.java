package ru.patterns.account.application.infrastructure;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CreditDeptScheduler {

    @Scheduled(fixedRate = 60000)
    public void performCreditDeptCalculation() {

    }
}
