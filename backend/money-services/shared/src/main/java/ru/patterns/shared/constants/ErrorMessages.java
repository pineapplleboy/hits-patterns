package ru.patterns.shared.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorMessages {
    public final String USER_NOT_FOUND = "User not found";
    public final String FORBIDDEN = "Forbidden";
    public final String ACCOUNT_NOT_FOUND = "Account not found";
    public final String UNAUTHORIZED = "Ошибка авторизации!";
    public final String INCORRECT_REQUEST_AMOUNT = "Incorrect request amount";
    public final String ACCOUNT_BANNED = "Account banned";
    public final String ONLY_WITH_RUB = "You can only pay credit with RUB";
    public final String TRANSFERS_BETWEEN_CURRENCIES_NOT_AVAILABLE = "Transfers between bank accounts to different person with different currency are not available";
    public final String SERVICE_CURRENTLY_UNAVAILABLE = "Service currently unavailable";
    public final String CREDIT_RATE_NOT_FOUND = "Credit rate not found";
    public final String CURRENCY_NOT_FOUND = "Currency not found";
    public final String INVALID_CREDIT_SUM = "Exceeded credit limit";
    public final String CURRENCY_NOT_SUPPORTABLE = "Currency is not supportable";
}
