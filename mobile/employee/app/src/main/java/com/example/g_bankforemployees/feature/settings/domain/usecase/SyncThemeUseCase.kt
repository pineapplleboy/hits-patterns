package com.example.g_bankforemployees.feature.settings.domain.usecase

import com.example.g_bankforemployees.common.presentation.theme.ThemeStorage
import com.example.g_bankforemployees.feature.settings.domain.repository.UserSettingsRepository

class SyncThemeUseCase(
    private val userSettingsRepository: UserSettingsRepository,
    private val themeStorage: ThemeStorage,
) {

    suspend operator fun invoke(): Result<Unit> =
        userSettingsRepository.getDarkTheme()
            .mapCatching { isDark ->
                themeStorage.setDarkTheme(isDark)
            }
}
