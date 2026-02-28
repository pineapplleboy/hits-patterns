package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.*
import retrofit2.http.*
import java.util.*

interface AccountService {
    
    // Bank Accounts endpoints
    @POST("/patterns/api/v1/users/{userId}/bank-accounts")
    suspend fun createBankAccount(
        @Path("userId") userId: UUID,
        @Body request: MoneyAmountRequestModel
    ): AccountNumberResponseModel
    
    @GET("/patterns/api/v1/users/{userId}/bank-accounts")
    suspend fun getUserBankAccounts(
        @Path("userId") userId: UUID
    ): List<BankAccountShortModel>
    
    @GET("/patterns/api/v1/users/{userId}/bank-accounts/{accountNumber}")
    suspend fun getBankAccountDetails(
        @Path("userId") userId: UUID,
        @Path("accountNumber") accountNumber: String
    ): BankAccountFullModel
    
    // Credit Accounts endpoints
    @GET("/patterns/api/v1/users/{userId}/credit-accounts")
    suspend fun getUserCreditAccounts(
        @Path("userId") userId: UUID
    ): List<CreditAccountShortModel>
    
    @GET("/patterns/api/v1/users/{userId}/credit-accounts/{accountNumber}")
    suspend fun getCreditAccountDetails(
        @Path("userId") userId: UUID,
        @Path("accountNumber") accountNumber: String
    ): CreditAccountFullModel
    
    // Operations endpoints
    @GET("/patterns/api/v1/users/{userId}/operations")
    suspend fun getUserOperations(
        @Path("userId") userId: UUID
    ): List<OperationModel>
    
    @GET("/patterns/api/v1/account-operations")
    suspend fun getAccountOperations(
        @Query("userId") userId: UUID,
        @Query("accountNumber") accountNumber: String,
        @Query("transferType") transferType: TransferAccountType
    ): List<OperationModel>
    
    @GET("/patterns/api/v1/users/{userId}/operations/{operationId}")
    suspend fun getOperationById(
        @Path("userId") userId: UUID,
        @Path("operationId") operationId: UUID
    ): OperationModel
    
    @GET("/patterns/api/v1/users/{userId}/operations/{operationId}/status")
    suspend fun getOperationStatus(
        @Path("userId") userId: UUID,
        @Path("operationId") operationId: UUID
    ): OperationStatusResponseModel
    
    // Transfers endpoints
    @POST("/patterns/api/v1/users/{userId}/transfers/bank-account/{bankAccountNumber}/replenish")
    suspend fun replenishBankAccount(
        @Path("userId") userId: UUID,
        @Path("bankAccountNumber") bankAccountNumber: String,
        @Body request: MoneyAmountRequestModel
    ): OperationStatusResponseModel
    
    @POST("/patterns/api/v1/users/{userId}/transfers/bank-account/{bankAccountNumber}/withdraw")
    suspend fun withdrawFromBankAccount(
        @Path("userId") userId: UUID,
        @Path("bankAccountNumber") bankAccountNumber: String,
        @Body request: MoneyAmountRequestModel
    ): OperationStatusResponseModel
    
    @POST("/patterns/api/v1/users/{userId}/transfers/bank-account/{bankAccountNumber}/credit-payments/{creditAccountNumber}")
    suspend fun payCreditFromBankAccount(
        @Path("userId") userId: UUID,
        @Path("bankAccountNumber") bankAccountNumber: String,
        @Path("creditAccountNumber") creditAccountNumber: String,
        @Body request: MoneyAmountRequestModel
    ): OperationStatusResponseModel
}
