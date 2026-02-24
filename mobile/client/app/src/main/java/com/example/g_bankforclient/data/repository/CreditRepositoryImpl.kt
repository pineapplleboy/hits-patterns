package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.common.models.Credit
import com.example.g_bankforclient.common.models.Transaction
import com.example.g_bankforclient.common.models.TransactionType
import com.example.g_bankforclient.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepositoryImpl @Inject constructor(
    private val accountRepository: AccountRepositoryImpl
) : CreditRepository {
    
    private val _credits = MutableStateFlow(
        listOf(
            Credit("1", "Ипотека", 5000000.0, 3200000.0, 8.5),
            Credit("2", "Автокредит", 800000.0, 320000.0, 12.0)
        )
    )

    override fun getCredits(): Flow<List<Credit>> = _credits.asStateFlow()

    override suspend fun createCredit(name: String, amount: Double, interestRate: Double) {
        _credits.value = _credits.value + Credit(
            id = System.currentTimeMillis().toString(),
            name = name,
            amount = amount,
            debt = amount,
            interestRate = interestRate
        )
    }

    override suspend fun payCredit(creditId: String, accountId: String, amount: Double) {
        val account = accountRepository.getAccounts()
        val credit = _credits.value.find { it.id == creditId }

        if (credit != null && credit.debt >= amount) {
            _credits.value = _credits.value.map { crd ->
                if (crd.id == creditId) {
                    crd.copy(debt = crd.debt - amount)
                } else {
                    crd
                }
            }
        }
    }
}
