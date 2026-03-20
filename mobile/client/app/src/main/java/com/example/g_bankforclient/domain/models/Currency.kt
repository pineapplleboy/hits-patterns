package com.example.g_bankforclient.domain.models

/** Валюта для выбора при открытии счёта. */
data class Currency(
    val id: Int,
    val name: String,
    val charCode: String,
    val symbol: String,
    val rate: Double
)
