package com.example.g_bankforemployees.feature.settings.di

import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import com.example.g_bankforemployees.common.presentation.theme.ThemeStorage
import com.example.g_bankforemployees.feature.settings.data.remote.UserSettingsApi
import com.example.g_bankforemployees.feature.settings.data.repository.UserSettingsRepositoryImpl
import com.example.g_bankforemployees.feature.settings.domain.repository.UserSettingsRepository
import com.example.g_bankforemployees.feature.settings.domain.usecase.SyncThemeUseCase
import com.example.g_bankforemployees.feature.settings.domain.usecase.UpdateThemeUseCase
import com.example.g_bankforemployees.feature.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val settingsModule = module {

    single<UserSettingsApi> {
        get<Retrofit>().create(UserSettingsApi::class.java)
    }

    single<UserSettingsRepository> {
        UserSettingsRepositoryImpl(userSettingsApi = get())
    }

    factory {
        SyncThemeUseCase(
            userSettingsRepository = get(),
            themeStorage = get<ThemeStorage>(),
        )
    }

    factory {
        UpdateThemeUseCase(
            userSettingsRepository = get(),
            themeStorage = get<ThemeStorage>(),
        )
    }

    viewModel {
        SettingsViewModel(
            themeStorage = get<ThemeStorage>(),
            updateThemeUseCase = get(),
            navigatorHolder = get<NavigatorHolder>(),
        )
    }
}
