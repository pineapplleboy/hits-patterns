package com.example.g_bankforemployees.feature.account_operations.data.remote

import com.example.g_bankforemployees.feature.account_operations.data.model.BankAccountFullDto
import com.example.g_bankforemployees.feature.account_operations.data.model.CreditAccountFullDto
import com.example.g_bankforemployees.feature.account_operations.data.model.OperationDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AccountOperationsApi {

    @GET("/core/patterns/api/v1/users/{userId}/bank-accounts/{accountNumber}")
    suspend fun getBankAccount(
        @Path("userId") userId: String,
        @Path("accountNumber") accountNumber: String,
    ): Response<BankAccountFullDto>

    @GET("/core/patterns/api/v1/users/{userId}/credit-accounts/{accountNumber}")
    suspend fun getCreditAccount(
        @Path("userId") userId: String,
        @Path("accountNumber") accountNumber: String,
    ): Response<CreditAccountFullDto>

    @GET("/core/patterns/api/v1/account-operations")
    suspend fun getAccountOperations(
        @Query("userId") userId: String,
        @Query("accountNumber") accountNumber: String,
        @Query("transferType") transferType: String,
    ): Response<List<OperationDto>>
}
