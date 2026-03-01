package com.example.g_bankforemployees.feature.credit_rate.domain.model

data class CreditRateInput(
    val name: String,
    val percent: Int,
    val writeOffPeriod: String,
)
