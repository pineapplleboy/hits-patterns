package com.example.g_bankforemployees.feature.account_operations.di

import androidx.lifecycle.SavedStateHandle
import com.example.g_bankforemployees.feature.account_operations.data.remote.AccountOperationsApi
import com.example.g_bankforemployees.feature.account_operations.data.repository.AccountOperationsRepositoryImpl
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetExpiredOperationsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.ObserveAccountOperationsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.ObserveBankAccountDetailsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.ObserveCreditAccountDetailsUseCase
import com.example.g_bankforemployees.feature.account_operations.presentation.AccountOperationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val accountOperationsModule = module {

    single<AccountOperationsApi> {
        get<Retrofit>().create(AccountOperationsApi::class.java)
    }

    single<AccountOperationsRepository> {
        AccountOperationsRepositoryImpl(
            accountOperationsApi = get(),
            realtimeSessionManager = get(),
            realtimeEventsRepository = get(),
        )
    }

    factory { ObserveBankAccountDetailsUseCase(accountOperationsRepository = get()) }
    factory { ObserveCreditAccountDetailsUseCase(accountOperationsRepository = get()) }
    factory { ObserveAccountOperationsUseCase(accountOperationsRepository = get()) }
    factory { GetExpiredOperationsUseCase(accountOperationsRepository = get()) }

    viewModel { (savedStateHandle: SavedStateHandle) ->
        AccountOperationsViewModel(
            savedStateHandle = savedStateHandle,
            observeAccountOperationsUseCase = get(),
            observeBankAccountDetailsUseCase = get(),
            observeCreditAccountDetailsUseCase = get(),
            getExpiredOperationsUseCase = get(),
            navigatorHolder = get(),
        )
    }
}
