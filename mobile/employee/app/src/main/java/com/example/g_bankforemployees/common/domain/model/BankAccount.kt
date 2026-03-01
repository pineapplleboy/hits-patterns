package com.example.g_bankforemployees.common.domain.model

data class BankAccount(
    val id: String,
    val accountNumber: String,
    val balance: Double,
    val banned: Boolean = false,
)
