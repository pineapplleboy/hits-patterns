package com.example.g_bankforemployees.common.di

import android.content.Context
import com.example.g_bankforemployees.common.presentation.theme.SharedPreferencesThemeStorage
import com.example.g_bankforemployees.common.presentation.theme.ThemeStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val themeModule = module {
    single<ThemeStorage> {
        SharedPreferencesThemeStorage(androidContext())
    }
}

