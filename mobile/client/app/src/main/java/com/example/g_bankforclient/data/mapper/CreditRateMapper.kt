package com.example.g_bankforclient.data.mapper

import com.example.g_bankforclient.data.network.model.CreditRateDataModel
import com.example.g_bankforclient.data.network.model.CreditRateModel
import com.example.g_bankforclient.domain.models.CreditRate

fun CreditRateModel.toDomain(): CreditRate {
    return CreditRate(
        rateId = rateId,
        name = name,
        percent = percent,
        writeOffPeriod = writeOffPeriod
    )
}

fun CreditRate.toNetwork(): CreditRateDataModel {
    return CreditRateDataModel(
        name = name,
        percent = percent,
        writeOffPeriod = writeOffPeriod
    )
}

