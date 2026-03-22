package com.example.g_bankforclient.data.network.model

/** Модель валюты из валютного сервиса (GET all-rates). */
data class CurrencyResponseModel(
    val id: Int,
    val name: String,
    val charCode: String,
    val symbol: String,
    val rate: Double
)
