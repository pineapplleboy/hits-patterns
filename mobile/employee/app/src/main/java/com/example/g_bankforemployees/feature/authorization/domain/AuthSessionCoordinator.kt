package com.example.g_bankforemployees.feature.authorization.domain

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class AuthSessionCoordinator {

    private val unauthorizedRedirectInProgress = AtomicBoolean(false)
    private val logoutInProgress = AtomicBoolean(false)
    private val pendingSsoError = AtomicReference<String?>(null)

    fun onAuthorized() {
        pendingSsoError.set(null)
        reset()
    }

    fun onLogoutStarted() {
        logoutInProgress.set(true)
        unauthorizedRedirectInProgress.set(true)
        pendingSsoError.set(null)
    }

    fun onLogoutFinished() {
        pendingSsoError.set(null)
        reset()
    }

    fun setPendingSsoError(message: String) {
        pendingSsoError.set(message)
    }

    fun consumePendingSsoError(): String? = pendingSsoError.getAndSet(null)

    fun tryStartUnauthorizedRedirect(): Boolean {
        if (logoutInProgress.get()) return false
        return unauthorizedRedirectInProgress.compareAndSet(false, true)
    }

    private fun reset() {
        logoutInProgress.set(false)
        unauthorizedRedirectInProgress.set(false)
    }
}
