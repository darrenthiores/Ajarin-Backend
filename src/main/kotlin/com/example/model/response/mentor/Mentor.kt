package com.example.model.response.mentor

import kotlinx.serialization.Serializable

@Serializable
data class Mentor(
    val id: String,
    val photoUrl: String,
    val name: String,
    val education: String,
    val rating: Double,
    val courses: List<Course>,
    val price: String,
    val priceCategory: String,
    val createdAt: Long,
    val modifiedAt: Long
)