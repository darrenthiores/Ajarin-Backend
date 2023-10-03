package com.example.data.table

import com.example.model.response.mentor.courseList
import com.example.model.response.order.Order
import com.example.model.response.order.paymentMethods
import com.example.model.response.order.sessions
import com.example.model.response.user.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Table.Dual.references

object OrderTable : Table("order") {
    val id = uuid("id").autoGenerate()
    val mentorId = uuid("mentorId").references(UserTable.id)
    val userId = uuid("userId").references(UserTable.id)
    val courseId = varchar("courseId", length = 255)
    val sessionId = varchar("sessionId", length = 255)
    val paymentMethod = varchar("paymentMethod", length = 255)
    val mentorPrice = varchar("mentorPrice", length = 255)
    val fee = varchar("fee", length = 255)
    val discount = varchar("discount", length = 255)
    val totalPrice = varchar("totalPrice", length = 255)
    val status = integer("status")
    val mainLink = varchar("mainLink", length = 255).nullable()
    val backupLink = varchar("backupLink", length = 255).nullable()
    val materialLink = varchar("materialLink", length = 255).nullable()
    val createdAt = long("createdAt")
    val modifiedAt = long("modifiedAt")

    override val primaryKey = PrimaryKey(id)

    fun ResultRow.toOrder(
        mentor: User,
        user: User
    ): Order {
        return Order(
            id = this[id].toString(),
            mentorId = this[mentorId].toString(),
            mentorName = mentor.username,
            mentorImgUrl = "",
            userId = this[userId].toString(),
            userName = user.username,
            userImgUrl = "",
            course = courseList.single { it.id == this[courseId] },
            schedule = sessions.single { it.id == this[sessionId] },
            date = this[createdAt],
            mentorPrice = this[mentorPrice],
            totalPrice = this[totalPrice],
            status = this[status].toString(),
            paymentMethod = paymentMethods.single { it.id == this[paymentMethod] }
        )
    }

    fun getStatusMessage(
        status: String
    ): String {
        return when(status) {
            "1" -> "Wait until your schedule!"
            "2" -> "Course On Going!"
            "3" -> "Rate your experience!"
            "4" -> "Session done!"
            else -> "Unknown Status Code!"
        }
    }

    fun getMentorStatusMessage(
        status: String
    ): String {
        return when(status) {
            "1" -> "Wait until your schedule!"
            "2" -> "Course On Going!"
            "3" -> "Session done!"
            "4" -> "Session done!"
            else -> "Unknown Status Code!"
        }
    }
}