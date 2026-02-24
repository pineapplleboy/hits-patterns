package com.example.g_bankforclient.di

import com.example.g_bankforclient.data.repository.AccountRepositoryImpl
import com.example.g_bankforclient.data.repository.AuthRepositoryImpl
import com.example.g_bankforclient.data.repository.CreditRepositoryImpl
import com.example.g_bankforclient.domain.repository.AccountRepository
import com.example.g_bankforclient.domain.repository.AuthRepository
import com.example.g_bankforclient.domain.repository.CreditRepository
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
}
