package com.example.g_bankforclient.data.network.model

import com.google.gson.annotations.SerializedName

data class CbrDailyResponse(
    @SerializedName("Valute") val valute: Map<String, CbrCurrencyRate>
)

data class CbrCurrencyRate(
    @SerializedName("CharCode") val charCode: String,
    @SerializedName("Nominal") val nominal: Int,
    @SerializedName("Name") val name: String,
    @SerializedName("Value") val value: Double
)
