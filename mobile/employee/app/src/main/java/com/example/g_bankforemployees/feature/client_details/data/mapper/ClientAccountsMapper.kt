package com.example.g_bankforemployees.feature.client_details.data.mapper

import com.example.g_bankforemployees.common.domain.model.BankAccount
import com.example.g_bankforemployees.common.domain.model.CreditAccount
import com.example.g_bankforemployees.feature.client_details.data.model.BankAccountShortDto
import com.example.g_bankforemployees.feature.client_details.data.model.CreditAccountShortDto

fun BankAccountShortDto.toDomain(): BankAccount = BankAccount(
    id = id,
    accountNumber = accountNumber,
    balance = balance,
    banned = banned,
)

fun CreditAccountShortDto.toDomain(): CreditAccount = CreditAccount(
    id = id,
    accountNumber = accountNumber,
    dept = dept,
    creditRateName = creditRateName,
    creditRatePercent = creditRatePercent,
    writeOffPeriod = writeOffPeriod,
    nextWriteOffDate = nextWriteOffDate,
    banned = banned,
)
