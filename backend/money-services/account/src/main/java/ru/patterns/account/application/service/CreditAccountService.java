package ru.patterns.account.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.patterns.account.domain.repository.CreditAccountRepository;

@Service
@RequiredArgsConstructor
public class CreditAccountService {

    private final CreditAccountRepository creditAccountRepository;


}
