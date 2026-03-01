package com.example.g_bankforemployees.feature.credit_rate.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreditRateDto(
    val rateId: String,
    val name: String,
    val percent: Int,
    val writeOffPeriod: String,
)

