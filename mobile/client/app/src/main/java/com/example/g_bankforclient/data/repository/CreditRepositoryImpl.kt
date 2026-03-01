package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.mapper.toCreditDomain
import com.example.g_bankforclient.data.mapper.toDomain
import com.example.g_bankforclient.data.network.AccountService
import com.example.g_bankforclient.data.network.ApiService
import com.example.g_bankforclient.data.network.model.MoneyAmountRequestModel
import com.example.g_bankforclient.data.network.model.TransferAccountType
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.CreditRate
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.repository.CreditRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val accountService: AccountService,
    private val tokenStorage: com.example.g_bankforclient.domain.TokenStorage
) : CreditRepository {

    private val currentUserId: UUID
        get() = tokenStorage.getUserId()
            ?.let { UUID.fromString(it) }
            ?: error("Пользователь не авторизован")

    override suspend fun getCredits(): List<Credit> {
        val creditAccounts = accountService.getUserCreditAccounts(currentUserId)

        return creditAccounts.map { creditAccount ->
            val creditOperations = try {
                accountService.getAccountOperations(
                    userId = currentUserId,
                    accountNumber = creditAccount.accountNumber,
                    transferType = TransferAccountType.CREDIT_ACCOUNT
                ).map { it.toCreditDomain(fallbackAccountId = creditAccount.accountNumber) }
            } catch (e: Exception) {
                emptyList()
            }

            creditAccount.toDomain(creditOperations)
        }
    }

    override suspend fun getCreditDetails(accountNumber: String): Credit {
        val creditDetails = accountService.getCreditAccountDetails(currentUserId, accountNumber)
        val transactions = creditDetails.operations.map {
            it.toCreditDomain(fallbackAccountId = accountNumber)
        }
        return creditDetails.toDomain(transactions)
    }

    override suspend fun getCreditTransactions(accountNumber: String): List<Transaction> {
        return accountService.getAccountOperations(
            userId = currentUserId,
            accountNumber = accountNumber,
            transferType = TransferAccountType.CREDIT_ACCOUNT
        ).map { it.toCreditDomain(fallbackAccountId = accountNumber) }
    }

    override suspend fun payCredit(creditId: String, accountId: String, amount: Double) {
        val response = accountService.payCreditFromBankAccount(
            userId = currentUserId,
            bankAccountNumber = accountId,
            creditAccountNumber = creditId,
            request = MoneyAmountRequestModel(amount = amount)
        )

        if (response.status.name != "SUCCESS") {
            throw Exception("Операция оплаты кредита в работе")
        }
    }
    
    override suspend fun getAvailableCreditRates(): List<CreditRate> {
        return apiService.getAvailableCreditPlans().map { it.toDomain() }
    }
    
    override suspend fun getAvailableCreditRateById(id: UUID): CreditRate {
        return apiService.getAvailableCreditPlanById(id).toDomain()
    }
    
    override suspend fun takeCredit(userId: UUID, rateId: UUID, sum: Double, bankAccountNum: String): Boolean {
        val response = apiService.takeCredit(userId, rateId, sum, bankAccountNum)
        return response.status.name == "SUCCESS"
    }
}
