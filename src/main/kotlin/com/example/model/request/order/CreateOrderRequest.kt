package com.example.model.request.order

import com.example.model.response.mentor.Course
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val mentorId: String,
    val courseId: String,
    val date: Long,
    val sessionId: String,
    val paymentMethodId: String
)
