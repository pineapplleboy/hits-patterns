package com.example.g_bankforemployees.feature.credit_history.data.mapper

import com.example.g_bankforemployees.feature.credit_history.data.model.CreditRatingDto
import com.example.g_bankforemployees.feature.credit_history.domain.model.CreditRating

fun CreditRatingDto.toDomain(): CreditRating = CreditRating(
    rating = rating,
    totalCreditCounter = totalCreditCounter,
    closedCreditCounter = closedCreditCounter,
    activeCreditAmount = activeCreditAmount,
    expiredOperationsAmount = expiredOperationsAmount,
)
