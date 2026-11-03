package com.example.labyshop

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class LogoutRequest(
    @SerialName("refresh_token")
    val refreshToken: String
)