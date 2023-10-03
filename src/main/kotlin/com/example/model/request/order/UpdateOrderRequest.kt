package com.example.model.request.order

import com.example.model.response.mentor.Course
import kotlinx.serialization.Serializable

@Serializable
data class UpdateOrderRequest(
    val id: String,
    val mainLink: String?,
    val backupLink: String?,
    val materialLink: String?,
    val status: Int?
)
