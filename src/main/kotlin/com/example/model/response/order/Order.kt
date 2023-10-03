package com.example.model.response.order

import com.example.model.response.mentor.Course
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String,
    val mentorId: String,
    val mentorName: String,
    val mentorImgUrl: String,
    val userId: String,
    val userName: String,
    val userImgUrl: String,
    val course: Course,
    val schedule: Session,
    val date: Long,
    val mentorPrice: String,
    val totalPrice: String,
    val status: String,
    val paymentMethod: PaymentMethod
)

@Serializable
data class PaymentMethod(
    val id: String,
    val name: String
)

val paymentMethods = listOf(
    PaymentMethod(
        id = "0",
        name = "Dana"
    ),
    PaymentMethod(
        id = "1",
        name = "Gopay"
    ),
    PaymentMethod(
        id = "2",
        name = "Shopee Pay"
    ),
    PaymentMethod(
        id = "3",
        name = "Ovo"
    )
)

@Serializable
data class Session(
    val id: String,
    val time: String // format hh:MM
) {
    companion object {
        fun currentSession(hour: Int): String {
            return when (hour) {
                in 8..9 -> "1"
                in 10..11 -> "2"
                in 12..13 -> "3"
                in 14..15 -> "4"
                in 16..17 -> "5"
                in 18..19 -> "6"
                in 20..21 -> "7"
                else -> "0"
            }
        }
    }
}

val sessions = listOf(
    Session(
        id = "1",
        time = "08:00 - 09:30"
    ),
    Session(
        id = "2",
        time = "10:00 - 11:30"
    ),
    Session(
        id = "3",
        time = "12:00 - 13:30"
    ),
    Session(
        id = "4",
        time = "14:00 - 15:30"
    ),
    Session(
        id = "5",
        time = "16:00 - 17:30"
    ),
    Session(
        id = "6",
        time = "18:00 - 19:30"
    ),
    Session(
        id = "7",
        time = "20:00 - 21:30"
    )
)