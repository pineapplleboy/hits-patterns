package com.example.g_bankforclient.feature.notifications.data.token

import android.content.Context
import com.example.g_bankforclient.feature.notifications.domain.token.NotificationTokenProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseNotificationTokenProvider @Inject constructor(
    @ApplicationContext private val context: Context,
) : NotificationTokenProvider {

    override suspend fun getNotificationToken(): String? =
        suspendCancellableCoroutine { continuation ->
            if (FirebaseApp.getApps(context).isEmpty()) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val task = runCatching { FirebaseMessaging.getInstance().token }
                .getOrElse { error ->
                    continuation.resumeWithException(error)
                    return@suspendCancellableCoroutine
                }

            task.addOnCompleteListener { result ->
                if (!continuation.isActive) return@addOnCompleteListener

                if (result.isSuccessful) {
                    continuation.resume(result.result)
                } else {
                    continuation.resumeWithException(
                        result.exception ?: IllegalStateException("Firebase token is unavailable"),
                    )
                }
            }
        }
}
