package com.example.g_bankforclient.domain.usecase.settings

import com.example.g_bankforclient.domain.models.UserSettings
import com.example.g_bankforclient.domain.repository.UserSettingsRepository
import javax.inject.Inject

class UpdateUserDarkModeUseCase @Inject constructor(
    private val repository: UserSettingsRepository
) {
    suspend operator fun invoke(isDarkMode: Boolean): UserSettings =
        repository.updateDarkMode(isDarkMode)
}
