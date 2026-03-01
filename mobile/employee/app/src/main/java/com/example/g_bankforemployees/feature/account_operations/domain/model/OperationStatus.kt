package com.example.g_bankforemployees.feature.account_operations.domain.model

enum class OperationStatus(val apiValue: String) {
    CREATED("CREATED"),
    IN_PROCESS("IN_PROCESS"),
    SUCCESS("SUCCESS"),
    REJECTED("REJECTED");

    companion object {
        fun fromApiValue(value: String): OperationStatus? = entries.find { it.apiValue == value }
    }
}
