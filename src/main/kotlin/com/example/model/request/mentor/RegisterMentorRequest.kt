package com.example.model.request.mentor

import kotlinx.serialization.Serializable

@Serializable
data class RegisterMentorRequest(
    val id: String,
    val education: String,
    val courses: List<String>,
    val price: String
)
