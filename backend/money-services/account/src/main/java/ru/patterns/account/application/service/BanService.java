package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.account.domain.repository.CreditAccountRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BanService {

    private final BankAccountRepository bankAccountRepository;
    private final CreditAccountRepository creditAccountRepository;

    public void banUserAccounts(UUID userId, boolean ban) {
        var userBankAccounts = bankAccountRepository.getBankAccountsByUserId(userId);
        var userCreditAccounts = creditAccountRepository.getCreditAccountByUserId(userId);

        userBankAccounts.forEach(bankAccount -> bankAccount.setActive(!ban));
        userCreditAccounts.forEach(creditAccount -> creditAccount.setActive(!ban));

        bankAccountRepository.saveAll(userBankAccounts);
        creditAccountRepository.saveAll(userCreditAccounts);
    }
}
