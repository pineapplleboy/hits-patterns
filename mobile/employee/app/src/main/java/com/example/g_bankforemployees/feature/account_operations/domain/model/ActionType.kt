package com.example.g_bankforemployees.feature.account_operations.domain.model

enum class ActionType(val apiValue: String) {
    OPEN_ACCOUNT("OPEN_ACCOUNT"),
    CLOSE_ACCOUNT("CLOSE_ACCOUNT"),
    TRANSFER("TRANSFER"),
    TRANSFER_RECEIVED("TRANSFER_RECEIVED"),
    TRANSFER_SENT("TRANSFER_SENT"),
    ACCOUNT_BANNED("ACCOUNT_BANNED"),
    ACCOUNT_UNBANNED("ACCOUNT_UNBANNED"),
    CREDIT_DEPT_PERCENT("CREDIT_DEPT_PERCENT");

    companion object {
        fun fromApiValue(value: String): ActionType? = entries.find { it.apiValue == value }
    }
}
