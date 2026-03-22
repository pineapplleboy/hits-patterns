package com.example.g_bankforemployees.feature.authorization.presentation

import android.app.Activity
import android.os.Bundle
import com.example.g_bankforemployees.feature.authorization.domain.AuthSessionCoordinator
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LogoutResultActivity : Activity(), KoinComponent {

    private val authSessionCoordinator: AuthSessionCoordinator by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authSessionCoordinator.onLogoutFinished()
        finish()
    }
}
