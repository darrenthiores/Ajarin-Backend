package com.example.model.response.mentor

import kotlinx.serialization.Serializable

@Serializable
data class Course(
    val id: String,
    val name: String
)

val courseList = listOf(
    Course(
        id = "1",
        name = "B. Indonesia"
    ),
    Course(
        id = "2",
        name = "Biologi"
    ),
    Course(
        id = "3",
        name = "Fisika"
    ),
    Course(
        id = "4",
        name = "Kimia"
    ),
    Course(
        id = "5",
        name = "Matematika"
    ),
    Course(
        id = "6",
        name = "Ekonomi"
    ),
    Course(
        id = "7",
        name = "Geografi"
    ),
    Course(
        id = "8",
        name = "Akuntansi"
    ),
    Course(
        id = "9",
        name = "Sejarah"
    ),
    Course(
        id = "10",
        name = "Sosiologi"
    )
)