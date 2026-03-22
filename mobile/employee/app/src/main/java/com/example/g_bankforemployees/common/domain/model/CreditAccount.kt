package com.example.g_bankforemployees.common.domain.model

data class CreditAccount(
    val id: String,
    val accountNumber: String,
    val dept: Double,
    val deptText: String? = null,
    val creditRateName: String,
    val creditRatePercent: Int,
    val writeOffPeriod: String,
    val nextWriteOffDate: String,
    val banned: Boolean = false,
)
