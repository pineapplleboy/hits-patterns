package com.example.g_bankforemployees.feature.client_details.data.remote

import com.example.g_bankforemployees.feature.client_details.data.model.BankAccountShortDto
import com.example.g_bankforemployees.feature.client_details.data.model.CreditAccountShortDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ClientAccountsApi {

    @GET("/core/patterns/api/v1/users/{userId}/bank-accounts")
    suspend fun getUserBankAccounts(
        @Path("userId") userId: String,
    ): Response<List<BankAccountShortDto>>

    @GET("/core/patterns/api/v1/users/{userId}/credit-accounts")
    suspend fun getUserCreditAccounts(
        @Path("userId") userId: String,
    ): Response<List<CreditAccountShortDto>>
}
