package com.example.g_bankforemployees.common.app

import android.app.Application
import com.example.g_bankforemployees.feature.authorization.di.authorizationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                authorizationModule,
            )
        }
    }
}