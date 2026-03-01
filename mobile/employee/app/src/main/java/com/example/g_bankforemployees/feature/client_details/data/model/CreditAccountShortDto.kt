package com.example.g_bankforemployees.feature.client_details.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreditAccountShortDto(
    val id: String,
    val accountNumber: String,
    val dept: Double,
    val creditRateName: String,
    val creditRatePercent: Int,
    val writeOffPeriod: String,
    val nextWriteOffDate: String,
    val banned: Boolean = false,
)
