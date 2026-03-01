package com.example.g_bankforemployees.feature.credit_rate.data.mapper

import com.example.g_bankforemployees.feature.credit_rate.data.model.CreditRateDto
import com.example.g_bankforemployees.feature.credit_rate.data.model.CreditRateDataDto
import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRate
import com.example.g_bankforemployees.feature.credit_rate.domain.model.CreditRateInput

fun CreditRateDto.toDomain(): CreditRate = CreditRate(
    id = rateId,
    name = name,
    percent = percent,
    writeOffPeriod = writeOffPeriod,
)

fun CreditRateInput.toDto(): CreditRateDataDto = CreditRateDataDto(
    name = name,
    percent = percent,
    writeOffPeriod = writeOffPeriod,
)
