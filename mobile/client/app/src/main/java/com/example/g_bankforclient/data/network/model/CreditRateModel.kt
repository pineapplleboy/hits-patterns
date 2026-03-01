package com.example.g_bankforclient.data.network.model

import java.util.UUID

data class CreditRateModel(
    val rateId: UUID,
    val name: String,
    val percent: Int,
    val writeOffPeriod: String
)
