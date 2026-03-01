package com.example.g_bankforemployees.feature.authorization.data.remote

import com.example.g_bankforemployees.feature.authorization.data.model.EmployeeLoginDto
import com.example.g_bankforemployees.feature.authorization.data.model.EmployeeRegisterDto
import com.example.g_bankforemployees.feature.authorization.data.model.RegisterDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/auth/patterns/api/v1/auth/employee-login")
    suspend fun employeeLogin(
        @Body body: EmployeeLoginDto,
    ): Response<String>

    @POST("/auth/patterns/api/v1/auth/employee-register-user")
    suspend fun employeeRegisterUser(
        @Body body: EmployeeRegisterDto,
    ): Response<Unit>

    @POST("/auth/patterns/api/v1/auth/employee-register")
    suspend fun employeeRegister(
        @Body body: RegisterDto,
    ): Response<String>
}
