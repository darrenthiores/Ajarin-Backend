package com.example.model.response.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    val username: String,
    val roleId: Int,
    val email: String,
    val number: String,
    val createdAt: Long,
    val modifiedAt: Long
)
