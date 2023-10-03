package com.example.data.table

import com.example.model.response.mentor.Mentor
import com.example.model.response.mentor.courseList
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table

object MentorTable : Table("mentor") {
    val id = uuid("id")
    val education = varchar("education", length = 255)
    val rating = double("rating")
    val ratingCount = integer("ratingCount")
    val courses = varchar("courses", length = 255)
    val price = varchar("price", length = 255)
    val priceCategory = varchar("priceCategory", length = 255)
    val createdAt = long("createdAt")
    val modifiedAt = long("modifiedAt")

    override val primaryKey = PrimaryKey(id)

    fun ResultRow.toMentor(
        name: String
    ): Mentor {
        val coursesId = this[courses]
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")

        return Mentor(
            id = this[id].toString(),
            photoUrl = "",
            name = name,
            education = this[education],
            rating = this[rating],
            courses = coursesId.mapNotNull { courseId ->
                courseList.firstOrNull { it.id == courseId }
            },
            price = this[price],
            priceCategory = this[priceCategory],
            createdAt = this[createdAt],
            modifiedAt = this[modifiedAt]
        )
    }

    fun getCategory(price: String): String? {
        val priceInt = price.toIntOrNull() ?: return null

        return when (priceInt) {
            in 0..49999 -> "<50k"
            in 50000..99999 -> "50k-100k"
            in 100000..199999 -> "100k-200k"
            else -> {
                if (priceInt > 200000) {
                    ">200k"
                } else null
            }
        }
    }
}