package ru.patterns.account.server.initialization;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.patterns.account.application.common.enums.BankAccountType;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.repository.BankAccountRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MasterBankAccountStarter implements ApplicationRunner {

    private final BankAccountRepository bankAccountRepository;

    @Value("${master-bank.account-number}")
    private String masterAccountNumber;

    @Value("${master-bank.money}")
    private BigDecimal masterAccountMoney;

    @Value("${master-bank.user-id}")
    private String masterAccountUserId;

    @Override
    public void run(ApplicationArguments args) {
        Optional<BankAccount> masterAccount = bankAccountRepository.getBankAccountByAccountNumberAndActive(masterAccountNumber, true);

        if (masterAccount.isPresent()) {
            log.info("Мастер-счёт уже зарегистрирован!");
            return;
        }

        BankAccount bankAccount = new BankAccount()
                .setUserId(UUID.fromString(masterAccountUserId))
                .setAccountNumber(masterAccountNumber)
                .setBalance(masterAccountMoney)
                .setAccountType(BankAccountType.MASTER)
                .setActive(true);

        bankAccountRepository.save(bankAccount);

        log.info("Мастер-счёт создан");
    }
}
