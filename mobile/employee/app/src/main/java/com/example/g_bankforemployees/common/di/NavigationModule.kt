package com.example.g_bankforemployees.common.di

import com.example.g_bankforemployees.common.navigation.NavigatorHolder
import org.koin.dsl.module

val navigationModule = module {
    single { NavigatorHolder }
}
