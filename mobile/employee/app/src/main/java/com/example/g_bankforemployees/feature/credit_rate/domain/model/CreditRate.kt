package com.example.g_bankforemployees.feature.credit_rate.domain.model

data class CreditRate(
    val id: String,
    val name: String,
    val percent: Int,
    val writeOffPeriod: String,
)

