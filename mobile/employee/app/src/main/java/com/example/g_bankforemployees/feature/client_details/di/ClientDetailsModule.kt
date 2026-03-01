package com.example.g_bankforemployees.feature.client_details.di

import androidx.lifecycle.SavedStateHandle
import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.client_details.data.remote.ClientAccountsApi
import com.example.g_bankforemployees.feature.client_details.data.repository.ClientAccountsRepositoryImpl
import com.example.g_bankforemployees.feature.client_details.domain.repository.ClientAccountsRepository
import com.example.g_bankforemployees.feature.client_details.domain.usecase.GetUserBankAccountsUseCase
import com.example.g_bankforemployees.feature.client_details.domain.usecase.GetUserCreditAccountsUseCase
import com.example.g_bankforemployees.feature.client_details.presentation.ClientDetailsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val clientDetailsModule = module {

    single<ClientAccountsApi> {
        get<Retrofit>().create(ClientAccountsApi::class.java)
    }

    single<ClientAccountsRepository> {
        ClientAccountsRepositoryImpl(clientAccountsApi = get())
    }

    factory { GetUserBankAccountsUseCase(clientAccountsRepository = get()) }
    factory { GetUserCreditAccountsUseCase(clientAccountsRepository = get()) }

    viewModel { (savedStateHandle: SavedStateHandle) ->
        ClientDetailsViewModel(
            savedStateHandle = savedStateHandle,
            getUserBankAccountsUseCase = get(),
            getUserCreditAccountsUseCase = get(),
            navigatorHolder = get(),
        )
    }
}
