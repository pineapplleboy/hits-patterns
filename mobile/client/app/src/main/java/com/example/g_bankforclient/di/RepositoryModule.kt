package com.example.g_bankforclient.di

import com.example.g_bankforclient.data.realtime.UserRealtimeRepositoryImpl
import com.example.g_bankforclient.data.repository.AccountRepositoryImpl
import com.example.g_bankforclient.data.repository.AuthRepositoryImpl
import com.example.g_bankforclient.data.repository.CreditRepositoryImpl
import com.example.g_bankforclient.data.repository.CurrencyRepositoryImpl
import com.example.g_bankforclient.data.repository.UserRepositoryImpl
import com.example.g_bankforclient.data.repository.UserSettingsRepositoryImpl
import com.example.g_bankforclient.domain.repository.AccountRepository
import com.example.g_bankforclient.domain.repository.AuthRepository
import com.example.g_bankforclient.domain.repository.CreditRepository
import com.example.g_bankforclient.domain.repository.CurrencyRepository
import com.example.g_bankforclient.domain.repository.UserRealtimeRepository
import com.example.g_bankforclient.domain.repository.UserRepository
import com.example.g_bankforclient.domain.repository.UserSettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAccountRepository(
        accountRepositoryImpl: AccountRepositoryImpl
    ): AccountRepository

    @Binds
    @Singleton
    abstract fun bindCreditRepository(
        creditRepositoryImpl: CreditRepositoryImpl
    ): CreditRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindCurrencyRepository(
        currencyRepositoryImpl: CurrencyRepositoryImpl
    ): CurrencyRepository

    @Binds
    @Singleton
    abstract fun bindUserSettingsRepository(
        userSettingsRepositoryImpl: UserSettingsRepositoryImpl
    ): UserSettingsRepository

    @Binds
    @Singleton
    abstract fun bindUserRealtimeRepository(
        userRealtimeRepositoryImpl: UserRealtimeRepositoryImpl
    ): UserRealtimeRepository
}
