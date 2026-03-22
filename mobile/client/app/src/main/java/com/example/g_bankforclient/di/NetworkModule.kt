package com.example.g_bankforclient.di

import android.content.Context
import com.example.g_bankforclient.data.network.AccountService
import com.example.g_bankforclient.data.network.ApiService
import com.example.g_bankforclient.data.network.AuthEventBus
import com.example.g_bankforclient.data.network.AuthInterceptor
import com.example.g_bankforclient.data.network.AuthService
import com.example.g_bankforclient.data.network.CurrencyService
import com.example.g_bankforclient.data.network.ExchangeRateService
import com.example.g_bankforclient.data.network.UserService
import com.example.g_bankforclient.data.network.UserSettingsService
import com.example.g_bankforclient.data.sso.AllowHttpConnectionBuilder
import com.example.g_bankforclient.data.token.SharedPreferencesAuthStateStorage
import com.example.g_bankforclient.data.token.SharedPreferencesTokenStorage
import com.example.g_bankforclient.domain.AuthStateStorage
import com.example.g_bankforclient.domain.TokenStorage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthorizationService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }
    
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
    
    @Provides
    @Singleton
    fun provideTokenStorage(@ApplicationContext context: Context): TokenStorage {
        return SharedPreferencesTokenStorage(context)
    }
    
    @Provides
    @Singleton
    fun provideAuthStateStorage(@ApplicationContext context: Context): AuthStateStorage {
        return SharedPreferencesAuthStateStorage(context)
    }

    @Provides
    @Singleton
    fun provideAppAuthConfiguration(): AppAuthConfiguration {
        return AppAuthConfiguration.Builder()
            .setConnectionBuilder(AllowHttpConnectionBuilder)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthorizationService(
        @ApplicationContext context: Context,
        appAuthConfiguration: AppAuthConfiguration
    ): AuthorizationService {
        return AuthorizationService(context, appAuthConfiguration)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        authStateStorage: AuthStateStorage,
        authorizationService: AuthorizationService,
        tokenStorage: TokenStorage,
        authEventBus: AuthEventBus
    ): AuthInterceptor {
        return AuthInterceptor(authStateStorage, authorizationService, tokenStorage, authEventBus)
    }
    
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }
    
    @Provides
    @Singleton
    @Named("creditRetrofit")
    fun provideCreditRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://91.227.18.176/credit/") // Credit service
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    @Named("coreRetrofit")
    fun provideCoreRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://91.227.18.176/core/") // Core service
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    @Named("authRetrofit")
    fun provideAuthRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://91.227.18.176/auth/") // Authorization service
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create()) // Для text/plain ответов (JWT токен)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    @Named("usersRetrofit")
    fun provideUsersRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://91.227.18.176/users/") // Users service
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("currencyRetrofit")
    fun provideCurrencyRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://91.227.18.176/currency/") // Currency service
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("settingsRetrofit")
    fun provideSettingsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://91.227.18.176/settings/") // Settings service
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("noAuthOkHttpClient")
    fun provideNoAuthOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("exchangeRateRetrofit")
    fun provideExchangeRateRetrofit(@Named("noAuthOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.cbr-xml-daily.ru/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideApiService(@Named("creditRetrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAccountService(@Named("coreRetrofit") retrofit: Retrofit): AccountService {
        return retrofit.create(AccountService::class.java)
    }

    @Provides
    @Singleton
    fun provideCurrencyService(@Named("currencyRetrofit") retrofit: Retrofit): CurrencyService {
        return retrofit.create(CurrencyService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserSettingsService(@Named("settingsRetrofit") retrofit: Retrofit): UserSettingsService {
        return retrofit.create(UserSettingsService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAuthService(@Named("authRetrofit") retrofit: Retrofit): AuthService {
        return retrofit.create(AuthService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideUserService(@Named("usersRetrofit") retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideExchangeRateService(@Named("exchangeRateRetrofit") retrofit: Retrofit): ExchangeRateService {
        return retrofit.create(ExchangeRateService::class.java)
    }
}
