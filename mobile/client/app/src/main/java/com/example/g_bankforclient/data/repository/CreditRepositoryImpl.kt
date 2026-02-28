package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.common.models.Credit
import com.example.g_bankforclient.common.models.CreditRate
import com.example.g_bankforclient.data.network.ApiService
import com.example.g_bankforclient.data.network.model.CreditRateDataModel
import com.example.g_bankforclient.domain.repository.CreditRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CreditRepositoryImpl @Inject constructor(
    private val apiService: ApiService
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
    
    override suspend fun getAvailableCreditRates(): List<CreditRate> {
        return try {
            val networkRates = apiService.getAvailableCreditPlans()
            networkRates.map { networkRate ->
                CreditRate(
                    rateId = networkRate.rateId,
                    name = networkRate.name,
                    percent = networkRate.percent,
                    writeOffPeriod = networkRate.writeOffPeriod
                )
            }
        } catch (e: Exception) {
            // Return empty list or handle error appropriately
            emptyList()
        }
    }
    
    override suspend fun getAvailableCreditRateById(id: UUID): CreditRate {
        val networkRate = apiService.getAvailableCreditPlanById(id)
        return CreditRate(
            rateId = networkRate.rateId,
            name = networkRate.name,
            percent = networkRate.percent,
            writeOffPeriod = networkRate.writeOffPeriod
        )
    }
    
    override suspend fun takeCredit(userId: UUID, rateId: UUID, sum: Double, bankAccountNum: String): Boolean {
        return try {
            val response = apiService.takeCredit(userId, rateId, sum, bankAccountNum)
            // Assuming SUCCESS status means the operation was successful
            response.status.name == "SUCCESS"
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun createCreditRate(creditRateData: CreditRate): UUID? {
        return try {
            val networkData = CreditRateDataModel(
                name = creditRateData.name,
                percent = creditRateData.percent,
                writeOffPeriod = creditRateData.writeOffPeriod
            )
            val response = apiService.createCreditRate(networkData)
            response.id
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun updateCreditRate(id: UUID, creditRateData: CreditRate): Boolean {
        return try {
            val networkData = CreditRateDataModel(
                name = creditRateData.name,
                percent = creditRateData.percent,
                writeOffPeriod = creditRateData.writeOffPeriod
            )
            apiService.updateCreditRate(id, networkData)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun deactivateCreditRate(id: UUID): Boolean {
        return try {
            apiService.deactivateCreditRate(id)
            true
        } catch (e: Exception) {
            false
        }
    }
}
