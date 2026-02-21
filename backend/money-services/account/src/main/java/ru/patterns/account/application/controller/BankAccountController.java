package ru.patterns.account.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.patterns.account.application.service.BankAccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bank-account")
public class BankAccountController {

    private final BankAccountService bankAccountService;

}
