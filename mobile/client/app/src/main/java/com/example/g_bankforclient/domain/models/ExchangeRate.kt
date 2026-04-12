package com.example.g_bankforclient.domain.models

data class ExchangeRate(
    val charCode: String,
    val name: String,
    val nominal: Int,
    val valueRub: Double
)
