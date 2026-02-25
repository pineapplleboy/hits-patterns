package ru.patterns.shared.utility;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MaskUtility {

    public String maskAccountNumber(String accountNumber) {
        return accountNumber.substring(accountNumber.length() - 4);
    }
}
