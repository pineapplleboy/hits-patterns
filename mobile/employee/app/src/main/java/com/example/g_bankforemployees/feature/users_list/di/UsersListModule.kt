package com.example.g_bankforemployees.feature.users_list.di

import com.example.g_bankforemployees.common.realtime.domain.RealtimeSessionManager
import com.example.g_bankforemployees.feature.notifications.domain.NotificationTokenSyncManager
import com.example.g_bankforemployees.feature.users_list.data.remote.UsersApi
import com.example.g_bankforemployees.feature.users_list.data.repository.UsersRepositoryImpl
import com.example.g_bankforemployees.feature.users_list.domain.repository.UsersRepository
import com.example.g_bankforemployees.feature.users_list.domain.usecase.GetUsersUseCase
import com.example.g_bankforemployees.feature.users_list.presentation.UsersListScreenViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

val usersListModule = module {

    single<UsersApi> {
        get<Retrofit>().create(UsersApi::class.java)
    }

    single<UsersRepository> {
        UsersRepositoryImpl(usersApi = get())
    }

    single {
        RealtimeSessionManager(
            realtimeEventsRepository = get(),
        )
    }

    factory {
        GetUsersUseCase(usersRepository = get())
    }

    viewModel {
        UsersListScreenViewModel(
            getUsersUseCase = get(),
            usersRepository = get(),
            context = androidContext(),
            tokenStorage = get(),
            authSessionCoordinator = get(),
            realtimeSessionManager = get(),
            notificationTokenSyncManager = get<NotificationTokenSyncManager>(),
            navigatorHolder = get(),
        )
    }
}

