package com.example.g_bankforemployees.feature.account_operations.di

import androidx.lifecycle.SavedStateHandle
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.account_operations.data.remote.AccountOperationsApi
import com.example.g_bankforemployees.feature.account_operations.data.repository.AccountOperationsRepositoryImpl
import com.example.g_bankforemployees.feature.account_operations.domain.repository.AccountOperationsRepository
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetAccountOperationsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetBankAccountDetailsUseCase
import com.example.g_bankforemployees.feature.account_operations.domain.usecase.GetCreditAccountDetailsUseCase
import com.example.g_bankforemployees.feature.account_operations.presentation.AccountOperationsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val accountOperationsModule = module {

    single<AccountOperationsApi> {
        get<Retrofit>().create(AccountOperationsApi::class.java)
    }

    single<AccountOperationsRepository> {
        AccountOperationsRepositoryImpl(accountOperationsApi = get())
    }

    factory { GetBankAccountDetailsUseCase(accountOperationsRepository = get()) }
    factory { GetCreditAccountDetailsUseCase(accountOperationsRepository = get()) }
    factory { GetAccountOperationsUseCase(accountOperationsRepository = get()) }

    viewModel { (savedStateHandle: SavedStateHandle) ->
        AccountOperationsViewModel(
            savedStateHandle = savedStateHandle,
            getAccountOperationsUseCase = get(),
            getBankAccountDetailsUseCase = get(),
            getCreditAccountDetailsUseCase = get(),
            navigatorHolder = get(),
        )
    }
}
