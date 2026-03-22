package com.example.g_bankforemployees.feature.settings.domain.repository

interface UserSettingsRepository {

    suspend fun getDarkTheme(): Result<Boolean>

    suspend fun updateDarkTheme(isDark: Boolean): Result<Boolean>
}
