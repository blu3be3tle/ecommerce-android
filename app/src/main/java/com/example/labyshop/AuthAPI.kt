package com.example.labyshop

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPI{
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}