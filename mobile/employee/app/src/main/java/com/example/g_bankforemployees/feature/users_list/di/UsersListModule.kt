package com.example.g_bankforemployees.feature.users_list.di

import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import com.example.g_bankforemployees.feature.credit_rate.domain.usecase.GetCreditRatesUseCase
import com.example.g_bankforemployees.feature.users_list.data.remote.UsersApi
import com.example.g_bankforemployees.feature.users_list.data.repository.UsersRepositoryImpl
import com.example.g_bankforemployees.feature.users_list.domain.repository.UsersRepository
import com.example.g_bankforemployees.feature.users_list.domain.usecase.GetUsersUseCase
import com.example.g_bankforemployees.feature.users_list.presentation.UsersListScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val usersListModule = module {

    single<UsersApi> {
        get<Retrofit>().create(UsersApi::class.java)
    }

    single<UsersRepository> {
        UsersRepositoryImpl(usersApi = get())
    }

    factory {
        GetUsersUseCase(usersRepository = get())
    }

    viewModel {
        UsersListScreenViewModel(
            getUsersUseCase = get(),
            usersRepository = get(),
            getCreditRatesUseCase = get(),
            tokenStorage = get(),
            navigatorHolder = get(),
        )
    }
}