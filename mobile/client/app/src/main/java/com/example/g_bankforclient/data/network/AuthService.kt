package com.example.g_bankforclient.data.network

import com.example.g_bankforclient.data.network.model.RegisterDTO
import com.example.g_bankforclient.data.network.model.UserLoginDTO
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/patterns/api/v1/auth/client-login")
    suspend fun clientLogin(@Body userLoginDTO: UserLoginDTO): String

    @POST("/patterns/api/v1/auth/client-register")
    suspend fun clientRegister(@Body registerDTO: RegisterDTO): String
}
