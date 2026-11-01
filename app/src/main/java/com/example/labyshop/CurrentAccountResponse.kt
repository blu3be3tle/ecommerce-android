package com.example.labyshop

import kotlinx.serialization.Serializable

@Serializable
data class CurrentAccountResponse(
    val success: Boolean,
    val account: Account
)