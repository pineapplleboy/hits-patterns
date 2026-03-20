package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.UserSettingsDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserSettingsService {

    @GET("patterns/api/v1/user/setting/my-settings")
    suspend fun getMySettings(): UserSettingsDTO

    @PUT("patterns/api/v1/user/setting/my-settings")
    suspend fun updateMySettings(@Body settings: UserSettingsDTO): UserSettingsDTO
}
