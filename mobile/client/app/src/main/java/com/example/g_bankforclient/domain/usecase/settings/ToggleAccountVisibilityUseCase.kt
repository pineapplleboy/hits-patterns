package com.example.g_bankforclient.domain.usecase.settings

import com.example.g_bankforclient.domain.repository.UserSettingsRepository
import javax.inject.Inject

class ToggleAccountVisibilityUseCase @Inject constructor(
    private val repository: UserSettingsRepository
) {
    suspend operator fun invoke(accountUuid: String) =
        repository.toggleAccountVisibility(accountUuid)
}
