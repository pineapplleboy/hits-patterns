package com.example.g_bankforclient.feature.notifications.data.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.g_bankforclient.MainActivity
import com.example.g_bankforclient.R
import com.example.g_bankforclient.domain.TokenStorage
import com.example.g_bankforclient.feature.notifications.domain.NotificationTokenSyncManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val DEFAULT_NOTIFICATION_TITLE = "G-Bank"
private const val DEFAULT_NOTIFICATION_BODY = "Новое уведомление"

@AndroidEntryPoint
class ClientFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationTokenSyncManager: NotificationTokenSyncManager

    @Inject
    lateinit var tokenStorage: TokenStorage

    override fun onNewToken(token: String) {
        if (tokenStorage.getToken().isNullOrBlank()) {
            return
        }

        notificationTokenSyncManager.register(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        showNotification(message)
    }

    private fun showNotification(message: RemoteMessage) {
        if (!canShowNotification()) return

        createClientNotificationChannel()
        val notificationId = message.messageId?.hashCode() ?: System.currentTimeMillis().toInt()
        val contentIntent = PendingIntent.getActivity(
            this,
            notificationId,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, CLIENT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(message.notification?.title ?: message.data["title"] ?: DEFAULT_NOTIFICATION_TITLE)
            .setContentText(message.notification?.body ?: message.data["body"] ?: message.data["message"] ?: DEFAULT_NOTIFICATION_BODY)
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

    private fun canShowNotification(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true

        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
