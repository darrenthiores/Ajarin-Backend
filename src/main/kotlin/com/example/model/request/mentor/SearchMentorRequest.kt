package com.example.model.request.mentor

import com.example.model.response.mentor.Course
import kotlinx.serialization.Serializable

@Serializable
data class SearchMentorRequest(
    val name: String,
    val education: String,
    val rating: Double,
    val courseId: String,
    val price: String
)
