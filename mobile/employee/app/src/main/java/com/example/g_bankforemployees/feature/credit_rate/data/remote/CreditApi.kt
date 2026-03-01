package com.example.g_bankforemployees.feature.credit_rate.data.remote

import com.example.g_bankforemployees.feature.credit_rate.data.model.CreditRateDataDto
import com.example.g_bankforemployees.feature.credit_rate.data.model.UuidResponseDto
import com.example.g_bankforemployees.feature.credit_rate.data.model.CreditRateDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CreditApi {

    @GET("/credit/patterns/api/v1/credit-rate/available-plans")
    suspend fun getCreditRates(): Response<List<CreditRateDto>>

    @POST("/credit/patterns/api/v1/credit-rate")
    suspend fun createCreditRate(
        @Body body: CreditRateDataDto,
    ): Response<UuidResponseDto>
}
