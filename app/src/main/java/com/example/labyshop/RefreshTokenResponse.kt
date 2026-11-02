package com.example.labyshop

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class RefreshTokenResponse(
    val success: Boolean,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("refresh_token")
    val refreshToken: String,
)