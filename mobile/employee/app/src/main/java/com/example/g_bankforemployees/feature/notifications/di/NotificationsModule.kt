package com.example.g_bankforemployees.feature.notifications.di

import com.example.g_bankforemployees.feature.notifications.data.remote.NotificationApi
import com.example.g_bankforemployees.feature.notifications.data.repository.NotificationRepositoryImpl
import com.example.g_bankforemployees.feature.notifications.data.token.FirebaseNotificationTokenProvider
import com.example.g_bankforemployees.feature.notifications.domain.NotificationTokenSyncManager
import com.example.g_bankforemployees.feature.notifications.domain.repository.NotificationRepository
import com.example.g_bankforemployees.feature.notifications.domain.token.NotificationTokenProvider
import com.example.g_bankforemployees.feature.notifications.domain.usecase.RegisterNotificationTokenUseCase
import com.example.g_bankforemployees.feature.notifications.domain.usecase.UnsubscribeNotificationTokenUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

val notificationsModule = module {

    single<NotificationApi> {
        get<Retrofit>().create(NotificationApi::class.java)
    }

    single<NotificationTokenProvider> {
        FirebaseNotificationTokenProvider(
            context = androidContext(),
        )
    }

    single<NotificationRepository> {
        NotificationRepositoryImpl(
            notificationApi = get(),
            notificationTokenProvider = get(),
            tokenStorage = get(),
        )
    }

    factory {
        RegisterNotificationTokenUseCase(notificationRepository = get())
    }

    factory {
        UnsubscribeNotificationTokenUseCase(notificationRepository = get())
    }

    single {
        NotificationTokenSyncManager(
            registerNotificationTokenUseCase = get(),
            unsubscribeNotificationTokenUseCase = get(),
        )
    }
}
