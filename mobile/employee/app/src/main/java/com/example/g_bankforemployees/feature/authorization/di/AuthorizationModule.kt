package com.example.g_bankforemployees.feature.authorization.di

import com.example.g_bankforemployees.feature.authorization.data.remote.AuthApi
import com.example.g_bankforemployees.feature.authorization.data.remote.AuthInterceptor
import com.example.g_bankforemployees.feature.authorization.data.repository.AuthRepositoryImpl
import com.example.g_bankforemployees.feature.authorization.data.token.SharedPreferencesTokenStorage
import com.example.g_bankforemployees.feature.authorization.domain.TokenStorage
import com.example.g_bankforemployees.feature.authorization.domain.repository.AuthRepository
import com.example.g_bankforemployees.feature.authorization.domain.usecase.CreateUserUseCase
import com.example.g_bankforemployees.feature.authorization.domain.usecase.EmployeeLoginUseCase
import com.example.g_bankforemployees.feature.authorization.domain.usecase.EmployeeRegisterUseCase
import com.example.g_bankforemployees.feature.authorization.presentation.AuthorizationScreenViewModel
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

val authorizationModule = module {

    single<TokenStorage> {
        SharedPreferencesTokenStorage(androidContext())
    }

    single {
        AuthInterceptor(tokenStorage = get())
    }

    single {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single {
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/")
            .client(get())
            .addConverterFactory(get<Json>().asConverterFactory(contentType))
            .build()
    }

    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            authApi = get(),
            tokenStorage = get(),
        )
    }

    factory {
        EmployeeLoginUseCase(
            authRepository = get(),
        )
    }

    factory {
        EmployeeRegisterUseCase(
            authRepository = get(),
        )
    }

    factory {
        CreateUserUseCase(authRepository = get())
    }

    viewModel {
        AuthorizationScreenViewModel(
            employeeLoginUseCase = get(),
            employeeRegisterUseCase = get()
        )
    }
}

