package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.UserSettingsDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserSettingsService {

    @GET("patterns/api/v1/user/setting/my-settings")
    suspend fun getMySettings(): UserSettingsDTO

    @PUT("patterns/api/v1/user/setting/my-settings")
    suspend fun updateMySettings(@Body settings: UserSettingsDTO): UserSettingsDTO

    @PUT("patterns/api/v1/user/setting/account-visibility/{accountId}")
    suspend fun toggleAccountVisibility(@Path("accountId") accountId: String)
}
