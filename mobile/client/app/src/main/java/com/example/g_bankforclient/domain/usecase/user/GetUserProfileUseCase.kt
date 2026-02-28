package com.example.g_bankforclient.domain.usecase.user

import com.example.g_bankforclient.common.models.User
import com.example.g_bankforclient.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User {
        return userRepository.getMyProfile()
    }
}
