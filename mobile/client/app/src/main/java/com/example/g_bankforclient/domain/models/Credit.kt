package com.example.g_bankforclient.domain.models

import java.util.Date

data class Credit(
    val id: String,
    val name: String,
    val amount: Double,
    var debt: Double,
    val interestRate: Double,
    val writeOffPeriod: String? = null,
    val nextWriteOffDate: Date? = null,
    val transactions: List<Transaction> = emptyList()
)

