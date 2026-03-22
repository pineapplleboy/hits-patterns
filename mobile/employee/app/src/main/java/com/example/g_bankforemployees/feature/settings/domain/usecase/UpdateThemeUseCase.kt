package com.example.g_bankforemployees.feature.settings.domain.usecase

import com.example.g_bankforemployees.common.presentation.theme.ThemeStorage
import com.example.g_bankforemployees.feature.settings.domain.repository.UserSettingsRepository

class UpdateThemeUseCase(
    private val userSettingsRepository: UserSettingsRepository,
    private val themeStorage: ThemeStorage,
) {

    suspend operator fun invoke(isDark: Boolean): Result<Unit> =
        userSettingsRepository.updateDarkTheme(isDark)
            .mapCatching { actualIsDark ->
                themeStorage.setDarkTheme(actualIsDark)
            }
}
