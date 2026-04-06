package ru.patterns.account.application.service.external;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.patterns.account.domain.entity.BankAccount;
import ru.patterns.account.domain.repository.BankAccountRepository;
import ru.patterns.shared.utility.RestClientRetryUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SettingsService {

    private final RestClient settingsClient;
    private final BankAccountRepository bankAccountRepository;

    public SettingsService(@Qualifier("settingsClient") RestClient settingsClient, BankAccountRepository bankAccountRepository) {
        this.settingsClient = settingsClient;
        this.bankAccountRepository = bankAccountRepository;
    }

    public List<BankAccount> getListOfHiddenBankAccounts(UUID userId, String token) {
        var hiddenIds = getHiddenAccountIds(userId, token);

        List<BankAccount> hiddenBankAccounts = new ArrayList<>();

        for (UUID accountId : hiddenIds) {
            var bankAccount = bankAccountRepository.findById(accountId);

            bankAccount.ifPresent(hiddenBankAccounts::add);
        }

        return hiddenBankAccounts;
    }

    public List<UUID> getHiddenAccountIds(UUID userId, String token) {
        return RestClientRetryUtility.execute(() -> settingsClient.get()
                .uri("/hidden-accounts/{userId}", userId)
                .header("Authorization", token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {}));
    }
}
