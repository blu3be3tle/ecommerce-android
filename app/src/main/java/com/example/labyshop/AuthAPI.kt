package com.example.labyshop

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthAPI{
    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("api/v1/auth/token/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): LoginResponse

    @GET("api/v1/auth/me")
    suspend fun getCurrentAccount(
        @Header("Authorization") authorization: String
    ): Account

}