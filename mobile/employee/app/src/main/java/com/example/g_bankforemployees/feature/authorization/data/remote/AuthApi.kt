package com.example.g_bankforemployees.feature.authorization.data.remote

import com.example.g_bankforemployees.feature.authorization.data.model.EmployeeLoginDto
import com.example.g_bankforemployees.feature.authorization.data.model.EmployeeRegisterDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("patterns/api/v1/auth/employee-login")
    suspend fun employeeLogin(
        @Body body: EmployeeLoginDto,
    ): Response<String>

    @POST("patterns/api/v1/auth/employee-register-user")
    suspend fun employeeRegister(
        @Body body: EmployeeRegisterDto,
    ): Response<Unit>
}
