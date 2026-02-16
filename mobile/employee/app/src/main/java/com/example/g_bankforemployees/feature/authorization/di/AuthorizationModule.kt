package com.example.g_bankforemployees.feature.authorization.di

import com.example.g_bankforemployees.feature.authorization.presentation.AuthorizationScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val authorizationModule = module {

//    single<AuthRepository> { AuthRepositoryImpl() }

//    factory { LoginUseCase(get()) }

    viewModel { AuthorizationScreenViewModel() }
}