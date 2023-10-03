package com.example.model.response.review

import kotlinx.serialization.Serializable

@Serializable
data class Review(
    val reviewId: String,
    val userId: String,
    val mentorId: String,
    val username: String,
    val userPhotoUrl: String,
    val rating: String,
    val comment: String,
    val imagesUrl: List<String>,
    val reviewDate: Long,
    val sessionId: String
)