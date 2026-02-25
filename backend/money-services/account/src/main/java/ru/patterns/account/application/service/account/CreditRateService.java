package ru.patterns.account.application.service.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.application.common.enums.AccountActionType;
import ru.patterns.account.application.service.operation.OperationHistoryService;
import ru.patterns.shared.model.enums.TransferAccountType;
import ru.patterns.account.application.utility.CreditUtility;
import ru.patterns.account.domain.entity.CreditAccount;
import ru.patterns.account.domain.repository.CreditAccountRepository;
import ru.patterns.shared.model.enums.OperationStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreditRateService {

    private final CreditAccountRepository creditAccountRepository;
    private final OperationHistoryService operationHistoryService;

    public void updateCreditAccounts() {
        var creditAccounts = getCreditAccounts();

        for (var creditAccount : creditAccounts) {

            while (creditAccount.getNextWriteOffDate().isBefore(Instant.now())) {
                BigDecimal creditGrowth = calculateCreditDeptGrowth(creditAccount);
                BigDecimal newDept = creditAccount.getDept().add(creditGrowth).setScale(2, RoundingMode.HALF_UP);

                creditAccount.setDept(newDept);
                creditAccount.setNextWriteOffDate(CreditUtility.calculateNextCreditWriteOffDate(creditAccount.getNextWriteOffDate(), creditAccount.getWriteOffPeriod()));

                creditAccountRepository.save(creditAccount);

                saveOperation(creditAccount, creditGrowth);
            }
        }
    }

    private List<CreditAccount> getCreditAccounts() {
        return creditAccountRepository
                .findAllByActiveIsTrueAndClosedIsFalseAndNextWriteOffDateLessThanEqual(Instant.now());
    }

    private BigDecimal calculateCreditDeptGrowth(CreditAccount creditAccount) {
        var annualRate = BigDecimal.valueOf(creditAccount.getCreditRatePercent())
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);

        var periodMinutes = BigDecimal.valueOf(creditAccount.getWriteOffPeriod().toMinutes());

        var periodRate = annualRate.multiply(periodMinutes)
                .divide(BigDecimal.valueOf(365 * 24 * 60), 10, RoundingMode.HALF_UP);

        var growth = creditAccount.getDept().multiply(periodRate);

        return growth.setScale(2, RoundingMode.HALF_UP);
    }

    private void saveOperation(CreditAccount creditAccount, BigDecimal growth) {
        operationHistoryService.createAndSaveOperation(
                creditAccount.getUserId(),
                TransferAccountType.CREDIT_ACCOUNT,
                growth,
                AccountActionType.TRANSFER_SENT,
                OperationStatus.SUCCESS);
    }
}
