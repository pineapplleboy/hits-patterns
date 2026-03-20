package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.CurrencyResponseModel
import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyService {

    @GET("patterns/api/v2/all-rates")
    suspend fun getAllCurrencies(): List<CurrencyResponseModel>

    @GET("patterns/api/v2/all-rates/{currencyId}")
    suspend fun getCurrency(@Path("currencyId") currencyId: Int): CurrencyResponseModel
}
