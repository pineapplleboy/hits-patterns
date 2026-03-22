package com.example.g_bankforemployees.feature.settings.data.repository

import com.example.g_bankforemployees.common.network.safeApiCall
import com.example.g_bankforemployees.feature.settings.data.model.UserSettingsDto
import com.example.g_bankforemployees.feature.settings.data.remote.UserSettingsApi
import com.example.g_bankforemployees.feature.settings.domain.repository.UserSettingsRepository

class UserSettingsRepositoryImpl(
    private val userSettingsApi: UserSettingsApi,
) : UserSettingsRepository {

    override suspend fun getDarkTheme(): Result<Boolean> =
        safeApiCall(
            apiCall = userSettingsApi::getMySettings,
            converter = UserSettingsDto::isDarkMode,
        )

    override suspend fun updateDarkTheme(isDark: Boolean): Result<Boolean> =
        safeApiCall(
            apiCall = {
                userSettingsApi.updateMySettings(
                    UserSettingsDto(isDarkMode = isDark),
                )
            },
            converter = UserSettingsDto::isDarkMode,
        )
}
