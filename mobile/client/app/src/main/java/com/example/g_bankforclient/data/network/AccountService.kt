package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.AccountNumberResponseModel
import com.example.g_bankforclient.data.network.model.BankAccountFullModel
import com.example.g_bankforclient.data.network.model.BankAccountShortModel
import com.example.g_bankforclient.data.network.model.CreditAccountFullModel
import com.example.g_bankforclient.data.network.model.CreditAccountShortModel
import com.example.g_bankforclient.data.network.model.MoneyAmountRequestModel
import com.example.g_bankforclient.data.network.model.OperationModel
import com.example.g_bankforclient.data.network.model.OperationStatusResponseModel
import com.example.g_bankforclient.data.network.model.TransferAccountType
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.UUID

interface AccountService {

    @POST("patterns/api/v1/users/{userId}/bank-accounts")
    suspend fun createBankAccount(
        @Path("userId") userId: UUID,
        @Body request: MoneyAmountRequestModel
    ): AccountNumberResponseModel

    @GET("patterns/api/v1/users/{userId}/bank-accounts")
    suspend fun getUserBankAccounts(
        @Path("userId") userId: UUID
    ): List<BankAccountShortModel>

    @GET("patterns/api/v1/users/{userId}/bank-accounts/{accountNumber}")
    suspend fun getBankAccountDetails(
        @Path("userId") userId: UUID,
        @Path("accountNumber") accountNumber: String
    ): BankAccountFullModel

    @DELETE("patterns/api/v1/users/{userId}/bank-accounts/{accountNumber}")
    suspend fun closeBankAccount(
        @Path("userId") userId: UUID,
        @Path("accountNumber") accountNumber: String
    )

    @GET("patterns/api/v1/users/{userId}/credit-accounts")
    suspend fun getUserCreditAccounts(
        @Path("userId") userId: UUID
    ): List<CreditAccountShortModel>

    @GET("patterns/api/v1/users/{userId}/credit-accounts/{accountNumber}")
    suspend fun getCreditAccountDetails(
        @Path("userId") userId: UUID,
        @Path("accountNumber") accountNumber: String
    ): CreditAccountFullModel

    @GET("patterns/api/v1/users/{userId}/operations")
    suspend fun getUserOperations(
        @Path("userId") userId: UUID
    ): List<OperationModel>

    @GET("patterns/api/v1/account-operations")
    suspend fun getAccountOperations(
        @Query("userId") userId: UUID,
        @Query("accountNumber") accountNumber: String,
        @Query("transferType") transferType: TransferAccountType
    ): List<OperationModel>

    @POST("patterns/api/v1/users/{userId}/transfers/bank-account/{bankAccountNumber}/replenish")
    suspend fun replenishBankAccount(
        @Path("userId") userId: UUID,
        @Path("bankAccountNumber") bankAccountNumber: String,
        @Body request: MoneyAmountRequestModel
    ): OperationStatusResponseModel

    @POST("patterns/api/v1/users/{userId}/transfers/bank-account/{bankAccountNumber}/withdraw")
    suspend fun withdrawFromBankAccount(
        @Path("userId") userId: UUID,
        @Path("bankAccountNumber") bankAccountNumber: String,
        @Body request: MoneyAmountRequestModel
    ): OperationStatusResponseModel

    @POST("patterns/api/v1/users/{userId}/transfers/bank-account/{bankAccountNumber}/credit-payments/{creditAccountNumber}")
    suspend fun payCreditFromBankAccount(
        @Path("userId") userId: UUID,
        @Path("bankAccountNumber") bankAccountNumber: String,
        @Path("creditAccountNumber") creditAccountNumber: String,
        @Body request: MoneyAmountRequestModel
    ): OperationStatusResponseModel
}
