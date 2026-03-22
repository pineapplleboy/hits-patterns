package com.example.g_bankforclient.data.network.model

import java.util.UUID

data class CreditRateModel(
    val rateId: UUID,
    val name: String,
    val percent: Int,
    val writeOffPeriod: String
)

/** Ответ /patterns/api/v1/credit-account/rating/{userId} */
data class CreditRatingNetworkModel(
    val rating: Long,
    val totalCreditCounter: Long,
    val closedCreditCounter: Long,
    val activeCreditAmount: Long,
    val expiredOperationsAmount: Long
)
