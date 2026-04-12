package com.example.g_bankforemployees.feature.authorization.di

import android.content.Context
import com.example.g_bankforemployees.common.network.CircuitBreakerInterceptor
import com.example.g_bankforemployees.common.network.CircuitBreakerRegistry
import com.example.g_bankforemployees.common.network.MonitoringInterceptor
import com.example.g_bankforemployees.common.network.NetworkFailureClassifier
import com.example.g_bankforemployees.common.network.NetworkResilienceConfig
import com.example.g_bankforemployees.common.network.RetryInterceptor
import com.example.g_bankforemployees.common.realtime.data.repository.StompRealtimeEventsRepository
import com.example.g_bankforemployees.common.realtime.domain.repository.RealtimeEventsRepository
import com.example.g_bankforemployees.feature.authorization.data.remote.AuthApi
import com.example.g_bankforemployees.feature.authorization.data.remote.AuthInterceptor
import com.example.g_bankforemployees.feature.authorization.data.repository.AuthRepositoryImpl
import com.example.g_bankforemployees.feature.authorization.data.token.SharedPreferencesTokenStorage
import com.example.g_bankforemployees.feature.authorization.domain.AuthSessionCoordinator
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import com.example.g_bankforemployees.feature.authorization.domain.repository.AuthRepository
import com.example.g_bankforemployees.feature.authorization.domain.usecase.CreateUserUseCase
import com.example.g_bankforemployees.feature.authorization.presentation.SsoGateViewModel
import com.example.g_bankforemployees.feature.authorization.presentation.SsoLoginViewModel
import com.example.g_bankforemployees.feature.notifications.domain.NotificationTokenSyncManager
import com.example.g_bankforemployees.feature.settings.domain.usecase.SyncThemeUseCase
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

val authorizationModule = module {

    single<TokenStorage> {
        SharedPreferencesTokenStorage(androidContext())
    }

    single {
        AuthSessionCoordinator()
    }

    single(named("apiLoggingInterceptor")) {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        MonitoringInterceptor(
            tokenStorage = get(),
        )
    }

    single {
        NetworkResilienceConfig()
    }

    single {
        val config = get<NetworkResilienceConfig>()
        NetworkFailureClassifier(config.retry)
    }

    single {
        val config = get<NetworkResilienceConfig>()
        CircuitBreakerRegistry(
            config = config.circuitBreaker,
        )
    }

    single {
        val config = get<NetworkResilienceConfig>()
        CircuitBreakerInterceptor(
            circuitBreakerRegistry = get(),
            failureClassifier = get(),
        )
    }

    single {
        val config = get<NetworkResilienceConfig>()
        RetryInterceptor(
            config = config.retry,
            failureClassifier = get(),
        )
    }

    single(named("apiOkHttpClient")) {
        OkHttpClient.Builder()
            .addInterceptor(get<MonitoringInterceptor>())
            .addInterceptor(get<RetryInterceptor>())
            .addInterceptor(get<CircuitBreakerInterceptor>())
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>(named("apiLoggingInterceptor")))
            .build()
    }

    single(named("stompOkHttpClient")) {
        OkHttpClient.Builder().build()
    }

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single<RealtimeEventsRepository> {
        StompRealtimeEventsRepository(
            okHttpClient = get(named("stompOkHttpClient")),
            json = get(),
            tokenStorage = get(),
        )
    }

    single {
        AuthInterceptor(
            tokenStorage = get(),
            authSessionCoordinator = get(),
            navigatorHolder = get(),
            realtimeSessionManager = get(),
        )
    }

    single {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("http://91.227.18.176/")
            .client(get(named("apiOkHttpClient")))
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(get<Json>().asConverterFactory(contentType))
            .build()
    }

    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
        )
    }

    factory {
        CreateUserUseCase(authRepository = get())
    }

    viewModel { (context: Context) ->
        SsoGateViewModel(
            tokenStorage = get(),
            context = context,
            authSessionCoordinator = get(),
            syncThemeUseCase = get<SyncThemeUseCase>(),
            notificationTokenSyncManager = get<NotificationTokenSyncManager>(),
            navigatorHolder = get(),
        )
    }

    viewModel {
        SsoLoginViewModel(
            tokenStorage = get(),
            authSessionCoordinator = get(),
            syncThemeUseCase = get<SyncThemeUseCase>(),
            navigatorHolder = get(),
        )
    }
}

