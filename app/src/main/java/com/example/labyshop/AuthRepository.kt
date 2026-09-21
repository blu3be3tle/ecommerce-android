package com.example.labyshop

class AuthRepository(
    private val api: AuthAPI
) {
    suspend fun login(
        identifier: String,
        password: String
    ): LoginResponse {

        val request = LoginRequest(
            identifier = identifier,
            password = password,
        )

        return api.login(request)
    }
}