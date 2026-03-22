package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.CbrDailyResponse
import retrofit2.http.GET

interface ExchangeRateService {
    @GET("daily_json.js")
    suspend fun getDailyRates(): CbrDailyResponse
}
