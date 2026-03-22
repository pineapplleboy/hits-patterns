package com.example.g_bankforemployees.feature.credit_history.di

import androidx.lifecycle.SavedStateHandle
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetExpiredOperationsUseCase
import com.example.g_bankforemployees.feature.credit_history.data.remote.CreditHistoryApi
import com.example.g_bankforemployees.feature.credit_history.data.repository.CreditHistoryRepositoryImpl
import com.example.g_bankforemployees.feature.credit_history.domain.repository.CreditHistoryRepository
import com.example.g_bankforemployees.feature.credit_history.domain.usecase.GetUserCreditRatingUseCase
import com.example.g_bankforemployees.feature.credit_history.presentation.CreditHistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val creditHistoryModule = module {

    single<CreditHistoryApi> {
        get<Retrofit>().create(CreditHistoryApi::class.java)
    }

    single<CreditHistoryRepository> {
        CreditHistoryRepositoryImpl(creditHistoryApi = get())
    }

    factory {
        GetUserCreditRatingUseCase(creditHistoryRepository = get())
    }

    viewModel { (savedStateHandle: SavedStateHandle) ->
        CreditHistoryViewModel(
            savedStateHandle = savedStateHandle,
            getUserCreditRatingUseCase = get(),
            getExpiredOperationsUseCase = get<GetExpiredOperationsUseCase>(),
            navigatorHolder = get(),
        )
    }
}
