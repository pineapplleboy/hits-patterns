package com.example.g_bankforemployees.feature.user_create.di

import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.feature.authorization.domain.usecase.CreateUserUseCase
import com.example.g_bankforemployees.feature.user_create.presentation.UserCreateScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val userCreateModule = module {
    viewModel {
        UserCreateScreenViewModel(
            createUserUseCase = get(),
            navigatorHolder = get(),
        )
    }
}
