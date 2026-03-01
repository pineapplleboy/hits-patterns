package com.example.g_bankforemployees.feature.users_list.data.remote

import com.example.g_bankforemployees.feature.users_list.data.model.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UsersApi {

    @GET("/users/patterns/api/v1/users/get-my-profile")
    suspend fun getMyProfile(): Response<UserDto>

    @GET("/users/patterns/api/v1/users/get-users")
    suspend fun getUsers(
        @Query("isClient") isClient: Boolean? = null,
    ): Response<List<UserDto>>

    @POST("/users/patterns/api/v1/users/ban-user/{id}")
    suspend fun banUser(@Path("id") id: String): Response<Unit>

    @POST("/users/patterns/api/v1/users/unban-user/{id}")
    suspend fun unbanUser(@Path("id") id: String): Response<Unit>
}
