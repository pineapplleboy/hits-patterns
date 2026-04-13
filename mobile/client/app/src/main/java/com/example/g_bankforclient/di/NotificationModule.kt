package com.example.g_bankforclient.di

import com.example.g_bankforclient.feature.notifications.data.remote.NotificationApi
import com.example.g_bankforclient.feature.notifications.data.repository.NotificationRepositoryImpl
import com.example.g_bankforclient.feature.notifications.data.token.FirebaseNotificationTokenProvider
import com.example.g_bankforclient.feature.notifications.domain.repository.NotificationRepository
import com.example.g_bankforclient.feature.notifications.domain.token.NotificationTokenProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationBindingsModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl,
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindNotificationTokenProvider(
        firebaseNotificationTokenProvider: FirebaseNotificationTokenProvider,
    ): NotificationTokenProvider
}

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationApi(
        @Named("notificationRetrofit") retrofit: Retrofit,
    ): NotificationApi = retrofit.create(NotificationApi::class.java)
}
