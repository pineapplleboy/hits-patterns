package com.example.g_bankforclient.data.sso

import kotlinx.coroutines.CancellableContinuation
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Keeps a single pending auth result callback while AppAuth redirects back into the app.
 * This project performs one login flow at a time, so a single slot dispatcher is sufficient.
 */
@Singleton
class SsoAuthResultDispatcher @Inject constructor() {
    private var pendingContinuation: CancellableContinuation<Boolean>? = null

    @Synchronized
    fun setPending(continuation: CancellableContinuation<Boolean>) {
        pendingContinuation = continuation
    }

    @Synchronized
    fun clearPending(continuation: CancellableContinuation<Boolean>) {
        if (pendingContinuation === continuation) {
            pendingContinuation = null
        }
    }

    @Synchronized
    fun complete(success: Boolean) {
        val cont = pendingContinuation
        pendingContinuation = null
        cont?.resume(success) // resume exactly once
    }
}

