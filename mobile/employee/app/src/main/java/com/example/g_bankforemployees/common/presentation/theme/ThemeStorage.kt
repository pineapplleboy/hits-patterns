package com.example.g_bankforemployees.common.presentation.theme

import kotlinx.coroutines.flow.StateFlow

interface ThemeStorage {
    val isDarkTheme: StateFlow<Boolean>

    fun setDarkTheme(isDark: Boolean)
}

