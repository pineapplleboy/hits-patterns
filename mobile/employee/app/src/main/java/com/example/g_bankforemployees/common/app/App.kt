package com.example.g_bankforemployees.common.app

import android.app.Application
import com.example.g_bankforemployees.common.di.navigationModule
import com.example.g_bankforemployees.feature.account_operations.di.accountOperationsModule
import com.example.g_bankforemployees.feature.authorization.di.authorizationModule
import com.example.g_bankforemployees.feature.client_details.di.clientDetailsModule
import com.example.g_bankforemployees.feature.credit_rate.di.creditRateModule
import com.example.g_bankforemployees.feature.user_create.di.userCreateModule
import com.example.g_bankforemployees.feature.users_list.di.usersListModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                navigationModule,
                authorizationModule,
                usersListModule,
                accountOperationsModule,
                clientDetailsModule,
                creditRateModule,
                userCreateModule,
            )
        }
    }
}