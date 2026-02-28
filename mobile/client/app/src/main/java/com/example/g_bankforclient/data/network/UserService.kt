package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.UserDB
import retrofit2.http.GET

interface UserService {
    
    @GET("/patterns/api/v1/users/get-my-profile")
    suspend fun getMyProfile(): UserDB
}
