package com.example.g_bankforemployees.feature.credit_rate.di

import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.credit_rate.data.remote.CreditApi
import com.example.g_bankforemployees.feature.credit_rate.data.repository.CreditRateRepositoryImpl
import com.example.g_bankforemployees.feature.credit_rate.domain.repository.CreditRateRepository
import com.example.g_bankforemployees.feature.credit_rate.domain.usecase.CreateCreditRateUseCase
import com.example.g_bankforemployees.feature.credit_rate.domain.usecase.GetCreditRatesUseCase
import com.example.g_bankforemployees.feature.credit_rate.presentation.CreditRateCreateScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val creditRateModule = module {

    single<CreditApi> {
        get<Retrofit>().create(CreditApi::class.java)
    }

    single<CreditRateRepository> {
        CreditRateRepositoryImpl(creditApi = get())
    }

    factory {
        CreateCreditRateUseCase(creditRateRepository = get())
    }

    factory {
        GetCreditRatesUseCase(creditRateRepository = get())
    }

    viewModel {
        CreditRateCreateScreenViewModel(
            createCreditRateUseCase = get(),
            navigatorHolder = get(),
        )
    }
}
