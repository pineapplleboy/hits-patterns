package com.example.g_bankforemployees.feature.account_operations.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CurrencyDto(
    val id: Int,
    val name: String,
    val charCode: String,
    val symbol: String,
    val rate: Double,
)
