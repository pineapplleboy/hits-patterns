package com.example.g_bankforclient.domain.usecase.settings

import com.example.g_bankforclient.domain.models.UserSettings
import com.example.g_bankforclient.domain.repository.UserSettingsRepository
import javax.inject.Inject

class GetUserSettingsUseCase @Inject constructor(
    private val repository: UserSettingsRepository
) {
    suspend operator fun invoke(): UserSettings = repository.getMySettings()
}
