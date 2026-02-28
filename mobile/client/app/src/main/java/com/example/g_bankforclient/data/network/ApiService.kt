package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.CreditRateDataModel
import com.example.g_bankforclient.data.network.model.CreditRateModel
import com.example.g_bankforclient.data.network.model.OperationStatusResponseModel
import com.example.g_bankforclient.data.network.model.UuidResponseModel
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface ApiService {
    
    @POST("/patterns/api/v1/credit-account/take/{userId}/{rateId}")
    suspend fun takeCredit(
        @Path("userId") userId: UUID,
        @Path("rateId") rateId: UUID,
        @Query("sum") sum: Double,
        @Query("bankAccountNum") bankAccountNum: String
    ): OperationStatusResponseModel
    
    @GET("/patterns/api/v1/credit-rate/available-plans")
    suspend fun getAvailableCreditPlans(): List<CreditRateModel>
    
    @GET("/patterns/api/v1/credit-rate/available-plans/{id}")
    suspend fun getAvailableCreditPlanById(
        @Path("id") id: UUID
    ): CreditRateModel
    
    @POST("/patterns/api/v1/credit-rate")
    suspend fun createCreditRate(
        @Body creditRateData: CreditRateDataModel
    ): UuidResponseModel
    
    @PUT("/patterns/api/v1/credit-rate/{id}")
    suspend fun updateCreditRate(
        @Path("id") id: UUID,
        @Body creditRateData: CreditRateDataModel
    )
    
    @DELETE("/patterns/api/v1/credit-rate/{id}")
    suspend fun deactivateCreditRate(
        @Path("id") id: UUID
    )
}
