package com.example.g_bankforemployees.feature.notifications.data.remote

import com.example.g_bankforemployees.feature.notifications.data.model.NotificationTokenRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface NotificationApi {

    @POST("/notification/patterns/api/v1/register")
    suspend fun registerToken(
        @Body body: NotificationTokenRequestDto,
    ): Response<Unit>

    @POST("/notification/patterns/api/v1/unsubscribe")
    suspend fun unsubscribeToken(
        @Body body: NotificationTokenRequestDto,
    ): Response<Unit>
}
