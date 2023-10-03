package com.example.model.response.authentication

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)
