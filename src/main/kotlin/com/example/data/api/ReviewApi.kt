package com.example.data.api

import com.example.data.table.MentorTable
import com.example.data.table.OrderTable
import com.example.data.table.ReviewTable
import com.example.data.table.UserTable
import com.example.model.base.BaseResponse
import com.example.model.base.MetaResponse
import com.example.model.request.id.IdRequest
import com.example.model.request.review.ReviewRequest
import com.example.model.response.review.Review
import com.example.util.query.dbQuery
import com.example.util.response.respondGeneral
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

object ReviewApi: KoinComponent {
    private val userTable by inject<UserTable>()
    private val mentorTable by inject<MentorTable>()
    private val orderTable by inject<OrderTable>()
    private val reviewTable by inject<ReviewTable>()

    fun Route.createReview(path: String) {
        post(path) {
            val request = call.receive<ReviewRequest>()

            dbQuery {
                reviewTable.insert {
                    it[orderId] = UUID.fromString(request.orderId)
                    it[rating] = request.rating
                    it[comment] = request.comment
                    it[imagesUrl] = ""
                    it[createdAt] = System.currentTimeMillis()
                    it[modifiedAt] = System.currentTimeMillis()
                }
            }.let {
                dbQuery {
                    orderTable
                        .select { orderTable.id eq UUID.fromString(request.orderId) }
                        .singleOrNull()
                }
                    ?.let { result ->
                        val mentorId = result[orderTable.mentorId]

                        dbQuery {
                            mentorTable
                                .select { mentorTable.id eq mentorId }
                                .singleOrNull()
                        }
                            ?.let { res ->
                                val mId = res[mentorTable.id]
                                val newCount = res[mentorTable.ratingCount] + 1
                                val newRating = (res[mentorTable.rating] * newCount + request.rating) / newCount

                                dbQuery {
                                    mentorTable
                                        .update(
                                            { mentorTable.id eq mId }
                                        ) {
                                            it[ratingCount] = newCount
                                            it[rating] = newRating
                                        }
                                }
                                    .let {
                                        call.respond(
                                            HttpStatusCode.OK,
                                            BaseResponse(
                                                meta = MetaResponse(
                                                    success = true,
                                                    message = "Review Posted"
                                                ),
                                                data = null
                                            )
                                        )
                                    }
                            }
                            ?: return@post respondGeneral(
                                success = false,
                                message = "Unknown Error",
                                code = HttpStatusCode.InternalServerError
                            )
                    }
                    ?: return@post respondGeneral(
                        success = false,
                        message = "Unknown Error",
                        code = HttpStatusCode.InternalServerError
                    )
            }
        }
    }

    fun Route.getMentorReviews(path: String) {
        post (path) {
            val request = call.receive<IdRequest>()
            val pageString = call.parameters["page"] ?: return@post call.respondText(
                "No page found",
                status = HttpStatusCode.BadRequest
            )
            val page = pageString.toIntOrNull() ?: return@post call.respondText(
                "No page found",
                status = HttpStatusCode.BadRequest
            )

            val pageSize = 15
            val skip = (page - 1) * pageSize

            dbQuery {
                reviewTable
                    .join(
                        OrderTable,
                        onColumn = ReviewTable.orderId,
                        otherColumn = OrderTable.id,
                        joinType = JoinType.INNER
                    )
                    .join(
                        UserTable,
                        onColumn = OrderTable.userId,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .select {
                        orderTable.mentorId eq UUID.fromString(request.id)
                    }
                    .limit(
                        n = pageSize,
                        offset = skip.toLong()
                    )
                    .map {
                        Review(
                            reviewId = it[reviewTable.id].toString(),
                            userId = it[orderTable.userId].toString(),
                            mentorId = it[orderTable.mentorId].toString(),
                            username = it[userTable.username],
                            userPhotoUrl = "",
                            rating = it[reviewTable.rating].toString(),
                            comment = it[reviewTable.comment],
                            imagesUrl = emptyList(),
                            reviewDate = it[reviewTable.createdAt],
                            sessionId = it[orderTable.sessionId]
                        )
                    }
            }.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve Mentor Reviews Success"
                        ),
                        data = it
                    )
                )
            }
        }
    }
}