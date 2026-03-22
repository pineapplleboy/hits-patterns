package com.example.g_bankforclient.data.repository

import com.example.g_bankforclient.data.network.UserSettingsService
import com.example.g_bankforclient.data.network.model.UserSettingsDTO
import com.example.g_bankforclient.domain.models.UserSettings
import com.example.g_bankforclient.domain.repository.UserSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSettingsRepositoryImpl @Inject constructor(
    private val userSettingsService: UserSettingsService
) : UserSettingsRepository {

    override suspend fun getMySettings(): UserSettings {
        val dto = userSettingsService.getMySettings()
        return UserSettings(isDarkMode = dto.isDarkMode)
    }

    override suspend fun updateDarkMode(isDarkMode: Boolean): UserSettings {
        val dto = userSettingsService.updateMySettings(UserSettingsDTO(isDarkMode = isDarkMode))
        return UserSettings(isDarkMode = dto.isDarkMode)
    }

    override suspend fun toggleAccountVisibility(accountUuid: String) {
        userSettingsService.toggleAccountVisibility(accountUuid)
    }
}
