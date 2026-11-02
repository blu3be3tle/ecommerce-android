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

    suspend fun refreshSession(): RefreshTokenResponse {
        val refreshToken = tokenStorage.getRefreshToken()
            ?: throw IllegalStateException("No refresh token available")

        val response = api.refreshToken(
            RefreshTokenRequest(refreshToken = refreshToken)
        )

        tokenStorage.saveTokens(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )

        return response
    }

    suspend fun hasValidSession(): Boolean {
        val accessToken = tokenStorage.getAccessToken()
            ?: return false

        return try {
            verifyAccessToken(accessToken)
        } catch (e: retrofit2.HttpException) {

            if (e.code() == 401) {
                throw e
            }

            try {
                val refreshed = refreshSession()
                if (!refreshed.success) {
                    tokenStorage.clearTokens()
                    return false
                }

                verifyAccessToken(refreshed.accessToken)

            } catch (refreshError: retrofit2.HttpException) {
                if (refreshError.code() == 401) {
                    tokenStorage.clearTokens()
                    false
                } else {
                    throw refreshError
                }
            }
        }
    }

    private suspend fun verifyAccessToken(
        accessToken: String
    ): Boolean {

            val response = api.getCurrentAccount(
                authorization = "Bearer $accessToken"
            )
            return response.success
        }



}