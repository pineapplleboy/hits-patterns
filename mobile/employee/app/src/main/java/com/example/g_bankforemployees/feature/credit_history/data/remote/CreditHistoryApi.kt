package com.example.g_bankforemployees.feature.credit_history.data.remote

import com.example.g_bankforemployees.feature.credit_history.data.model.CreditRatingDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CreditHistoryApi {

    @GET("/credit/patterns/api/v1/credit-account/rating/{userId}")
    suspend fun getUserCreditRating(
        @Path("userId") userId: String,
    ): Response<CreditRatingDto>
}
