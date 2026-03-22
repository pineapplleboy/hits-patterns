package com.example.g_bankforemployees.feature.settings.data.remote

import com.example.g_bankforemployees.feature.settings.data.model.UserSettingsDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface UserSettingsApi {

    @GET("/settings/patterns/api/v1/user/setting/my-settings")
    suspend fun getMySettings(): Response<UserSettingsDto>

    @PUT("/settings/patterns/api/v1/user/setting/my-settings")
    suspend fun updateMySettings(
        @Body body: UserSettingsDto,
    ): Response<UserSettingsDto>
}
