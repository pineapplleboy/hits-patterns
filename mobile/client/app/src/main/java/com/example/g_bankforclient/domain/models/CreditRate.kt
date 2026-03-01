package com.example.g_bankforclient.domain.models

import java.util.UUID

data class CreditRate(
    val rateId: UUID,
    val name: String,
    val percent: Int,
    val writeOffPeriod: String
)

