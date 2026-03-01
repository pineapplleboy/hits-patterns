package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.CreditRateModel
import com.example.g_bankforclient.data.network.model.OperationStatusResponseModel
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface ApiService {

    @POST("patterns/api/v1/credit-account/take/{userId}/{rateId}")
    suspend fun takeCredit(
        @Path("userId") userId: UUID,
        @Path("rateId") rateId: UUID,
        @Query("sum") sum: Double,
        @Query("bankAccountNum") bankAccountNum: String
    ): OperationStatusResponseModel

    @GET("patterns/api/v1/credit-rate/available-plans")
    suspend fun getAvailableCreditPlans(): List<CreditRateModel>

    @GET("patterns/api/v1/credit-rate/available-plans/{id}")
    suspend fun getAvailableCreditPlanById(
        @Path("id") id: UUID
    ): CreditRateModel
}
