package com.example.g_bankforemployees.common.domain.model

data class BankAccount(
    val id: String,
    val accountNumber: String,
    val balance: Double,
    val balanceText: String? = null,
    val banned: Boolean = false,
    val hidden: Boolean = false,
    val createTime: String? = null,
    val currency: Currency? = null,
)
