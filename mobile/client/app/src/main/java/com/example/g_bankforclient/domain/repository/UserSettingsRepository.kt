package com.example.g_bankforclient.domain.repository

import com.example.g_bankforclient.domain.models.UserSettings

interface UserSettingsRepository {
    suspend fun getMySettings(): UserSettings
    suspend fun updateDarkMode(isDarkMode: Boolean): UserSettings
    suspend fun toggleAccountVisibility(accountUuid: String)
}
