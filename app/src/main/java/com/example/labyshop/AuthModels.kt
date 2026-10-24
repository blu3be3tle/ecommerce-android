package com.example.labyshop

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

@Serializable
data class LoginResponse(
    val success: Boolean,
    val account: Account,
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("refresh_family")
    val refreshFamily: String,
)

@Serializable
data class Account(
    val id: String,
    @SerialName("legacy_account_id")
    val legacyAccountId: Int,
    val name: String,
    val email: String,
    val phone: String,
    @SerialName("email_verified")
    val emailVerified: Boolean,
    @SerialName("phone_verified")
    val phoneVerified: Boolean,
    val status: String
)