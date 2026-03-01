package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.UserDTO
import retrofit2.http.GET

interface UserService {

    @GET("patterns/api/v1/users/get-my-profile")
    suspend fun getMyProfile(): UserDTO
}
