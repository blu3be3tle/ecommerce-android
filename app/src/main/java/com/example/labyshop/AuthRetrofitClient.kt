package com.example.labyshop

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory


object AuthRetrofitClient {
    private const val BASE_URL = "https://api.auth.shajme.com"

    private val json = Json {
        ignoreUnknownKeys = true
    }

    val api: AuthAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create(AuthAPI::class.java)
    }
}