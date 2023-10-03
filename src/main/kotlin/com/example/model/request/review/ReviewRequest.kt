package com.example.model.request.review

import kotlinx.serialization.Serializable

@Serializable
data class ReviewRequest(
    val orderId: String,
    val comment: String,
    val rating: Double
)
