package com.example.g_bankforclient

import android.app.Application
import com.example.g_bankforclient.feature.notifications.data.service.createClientNotificationChannel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BankApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
        createClientNotificationChannel()
    }

    private fun initializeFirebase() {
        runCatching {
            if (FirebaseApp.getApps(this).isNotEmpty()) {
                return
            }

            FirebaseApp.initializeApp(this)
        }
    }
}
