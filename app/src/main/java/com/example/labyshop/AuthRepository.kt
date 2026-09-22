package com.example.labyshop

class AuthRepository(
    private val api: AuthAPI,
    private val tokenStorage: TokenStorage,
) {
    suspend fun login(
        identifier: String,
        password: String
    ): LoginResponse {

        val request = LoginRequest(
            identifier = identifier,
            password = password
        )

        val response = api.login(request)

        tokenStorage.saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )

        return response
    }
}