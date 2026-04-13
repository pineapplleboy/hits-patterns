package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.mapper.toCreditDomain
import com.example.g_bankforclient.data.mapper.toDomain
import com.example.g_bankforclient.data.network.AccountService
import com.example.g_bankforclient.data.network.ApiService
import com.example.g_bankforclient.data.network.RequestMetadataFactory
import com.example.g_bankforclient.data.network.model.MoneyAmountRequestModel
import com.example.g_bankforclient.data.network.model.TransferAccountType
import com.example.g_bankforclient.domain.models.Credit
import com.example.g_bankforclient.domain.models.CreditRate
import com.example.g_bankforclient.domain.models.CreditRating
import com.example.g_bankforclient.domain.models.CreditRatingLevel
import com.example.g_bankforclient.domain.models.Transaction
import com.example.g_bankforclient.domain.repository.CreditRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val accountService: AccountService,
    private val tokenStorage: com.example.g_bankforclient.domain.TokenStorage,
    private val requestMetadataFactory: RequestMetadataFactory
) : CreditRepository {

    private val currentUserId: UUID
        get() = tokenStorage.getUserId()
            ?.let { UUID.fromString(it) }
            ?: error("Пользователь не авторизован")

    override suspend fun getCredits(): List<Credit> {
        val creditAccounts = accountService.getUserCreditAccounts(currentUserId)

        return creditAccounts.map { creditAccount ->
            val creditOperations = accountService.getAccountOperations(
                userId = currentUserId,
                accountNumber = creditAccount.accountNumber,
                transferType = TransferAccountType.CREDIT_ACCOUNT
            ).map { it.toCreditDomain(fallbackAccountId = creditAccount.accountNumber) }

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
            request = MoneyAmountRequestModel(amount = amount),
            metadata = requestMetadataFactory.createMutation()
        )

        if (response.status.name != "SUCCESS") {
            throw com.example.g_bankforclient.domain.models.OperationPendingException("Платёж принят и выполняется")
        }
    }
    
    override suspend fun getAvailableCreditRates(): List<CreditRate> {
        return apiService.getAvailableCreditPlans().map { it.toDomain() }
    }
    
    override suspend fun getAvailableCreditRateById(id: UUID): CreditRate {
        return apiService.getAvailableCreditPlanById(id).toDomain()
    }
    
    override suspend fun takeCredit(userId: UUID, rateId: UUID, sum: Double, bankAccountNum: String): Boolean {
        val response = apiService.takeCredit(
            userId = userId,
            rateId = rateId,
            sum = sum,
            bankAccountNum = bankAccountNum,
            metadata = requestMetadataFactory.createMutation()
        )
        return response.status.name == "SUCCESS"
    }

    override suspend fun getCreditRating(): CreditRating {
        val response = apiService.getUserCreditRating(currentUserId)
        val score = response.rating.coerceIn(1, 999).toInt()
        val level = when {
            score >= 750 -> CreditRatingLevel.EXCELLENT
            score >= 500 -> CreditRatingLevel.GOOD
            score >= 250 -> CreditRatingLevel.FAIR
            else -> CreditRatingLevel.POOR
        }
        return CreditRating(
            score = score,
            level = level,
            totalCredits = response.totalCreditCounter,
            closedCredits = response.closedCreditCounter,
            activeCredits = response.activeCreditAmount,
            missedPayments = response.expiredOperationsAmount
        )
    }
}
