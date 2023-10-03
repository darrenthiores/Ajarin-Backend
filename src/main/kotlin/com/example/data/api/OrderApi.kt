package com.example.data.api

import com.example.data.table.MentorTable
import com.example.data.table.MentorTable.toMentor
import com.example.data.table.OrderTable
import com.example.data.table.OrderTable.toOrder
import com.example.data.table.UserTable
import com.example.data.table.UserTable.toUser
import com.example.model.base.BaseResponse
import com.example.model.base.MetaResponse
import com.example.model.request.id.IdRequest
import com.example.model.request.order.CreateOrderRequest
import com.example.model.request.order.UpdateOrderRequest
import com.example.model.response.user.User
import com.example.util.query.dbQuery
import com.example.util.response.respondGeneral
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object OrderApi: KoinComponent {
    private val mentorTable by inject<MentorTable>()
    private val orderTable by inject<OrderTable>()

    fun Route.createOrder(path: String) {
        post(path) {
            val request = call.receive<CreateOrderRequest>()
            val id = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@post call.respondText(
                "Unknown Error",
                status = HttpStatusCode.InternalServerError
            )

            dbQuery {
                mentorTable.select {
                    mentorTable.id eq UUID.fromString(request.mentorId)
                }.singleOrNull()
                    ?.toMentor(
                        name = ""
                    )
            }?.let { mentor ->
                dbQuery {
                    orderTable.insert {
                        it[mentorId] = UUID.fromString(request.mentorId)
                        it[userId] = UUID.fromString(id)
                        it[courseId] = request.courseId
                        it[sessionId] = request.sessionId
                        it[paymentMethod] = request.paymentMethodId
                        it[mentorPrice] = mentor.price
                        it[fee] = ((mentor.price.toIntOrNull() ?: 0) * 0.1).toString()
                        it[discount] = "0"
                        it[totalPrice] = ((mentor.price.toIntOrNull() ?: 0) + (mentor.price.toIntOrNull() ?: 0) * 0.1).toString()
                        it[status] = 1
                        it[createdAt] = System.currentTimeMillis()
                        it[modifiedAt] = System.currentTimeMillis()
                    }
                }.let {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            meta = MetaResponse(
                                success = true,
                                message = "Order Created"
                            ),
                            data = null
                        )
                    )
                }
            } ?: return@post respondGeneral(
                success = false,
                message = "Mentor not found",
                code = HttpStatusCode.BadRequest
            )
        }
    }

    fun Route.updateOrder(path: String) {
        post(path) {
            val request = call.receive<UpdateOrderRequest>()

            dbQuery {
                orderTable.update(
                    { orderTable.id eq UUID.fromString(request.id) }
                ) {
                    request.mainLink?.let { link ->
                        it[orderTable.mainLink] = link
                    }
                    request.backupLink?.let { link ->
                        it[orderTable.backupLink] = link
                    }
                    request.materialLink?.let { link ->
                        it[orderTable.materialLink] = link
                    }
                    request.status?.let { status ->
                        it[orderTable.status] = status
                    }
                }
            }.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Update Order Success"
                        ),
                        data = null
                    )
                )
            }
        }
    }

    fun Route.getOrdersAsUser(path: String) {
        get(path) {
            val id = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@get call.respondText(
                "Unknown Error",
                status = HttpStatusCode.InternalServerError
            )
            val pageString = call.parameters["page"] ?: return@get call.respondText(
                "No page found",
                status = HttpStatusCode.BadRequest
            )
            val page = pageString.toIntOrNull() ?: return@get call.respondText(
                "No page found",
                status = HttpStatusCode.BadRequest
            )

            val pageSize = 15
            val skip = (page - 1) * pageSize
            val mentor = UserTable.alias("MentorTable")

            dbQuery {
                orderTable
                    .join(
                        UserTable,
                        onColumn = OrderTable.userId,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .join(
                        mentor,
                        onColumn = OrderTable.mentorId,
                        otherColumn = mentor[UserTable.id],
                        joinType = JoinType.INNER
                    )
                    .select {
                        orderTable.userId eq UUID.fromString(id)
                    }
                    .limit(
                        n = pageSize,
                        offset = skip.toLong()
                    )
                    .map {
                        it.toOrder(
                            mentor = User(
                                id = it[mentor[UserTable.id]].toString(),
                                username = it[mentor[UserTable.username]],
                                roleId = it[mentor[UserTable.roleId]],
                                email = it[mentor[UserTable.email]],
                                number = it[mentor[UserTable.number]],
                                createdAt = it[mentor[UserTable.createdAt]],
                                modifiedAt = it[mentor[UserTable.modifiedAt]]
                            ),
                            user = it.toUser()
                        )
                    }
            }.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve Orders as User Success"
                        ),
                        data = it
                    )
                )
            }
        }
    }

    fun Route.getOrdersAsMentor(path: String) {
        get(path) {
            val id = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@get call.respondText(
                "Unknown Error",
                status = HttpStatusCode.InternalServerError
            )
            val pageString = call.parameters["page"] ?: return@get call.respondText(
                "No page found",
                status = HttpStatusCode.BadRequest
            )
            val page = pageString.toIntOrNull() ?: return@get call.respondText(
                "No page found",
                status = HttpStatusCode.BadRequest
            )

            val pageSize = 15
            val skip = (page - 1) * pageSize
            val mentor = UserTable.alias("MentorTable")

            dbQuery {
                orderTable
                    .join(
                        UserTable,
                        onColumn = OrderTable.userId,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .join(
                        mentor,
                        onColumn = OrderTable.mentorId,
                        otherColumn = mentor[UserTable.id],
                        joinType = JoinType.INNER
                    )
                    .select {
                        orderTable.mentorId eq UUID.fromString(id)
                    }
                    .limit(
                        n = pageSize,
                        offset = skip.toLong()
                    )
                    .map {
                        it.toOrder(
                            mentor = User(
                                id = it[mentor[UserTable.id]].toString(),
                                username = it[mentor[UserTable.username]],
                                roleId = it[mentor[UserTable.roleId]],
                                email = it[mentor[UserTable.email]],
                                number = it[mentor[UserTable.number]],
                                createdAt = it[mentor[UserTable.createdAt]],
                                modifiedAt = it[mentor[UserTable.modifiedAt]]
                            ),
                            user = it.toUser()
                        )
                    }
            }.let {
                print(it)

                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve Orders as Mentor Success"
                        ),
                        data = it
                    )
                )
            }
        }
    }

    fun Route.getOrderById(path: String) {
        post(path) {
            val request = call.receive<IdRequest>()
            val mentor = UserTable.alias("MentorTable")

            dbQuery {
                orderTable
                    .join(
                        UserTable,
                        onColumn = OrderTable.userId,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .join(
                        mentor,
                        onColumn = OrderTable.mentorId,
                        otherColumn = mentor[UserTable.id],
                        joinType = JoinType.INNER
                    )
                    .select {
                        orderTable.id eq UUID.fromString(request.id)
                    }
                    .singleOrNull()
            }?.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve Order Success"
                        ),
                        data = it.toOrder(
                            mentor = User(
                                id = it[mentor[UserTable.id]].toString(),
                                username = it[mentor[UserTable.username]],
                                roleId = it[mentor[UserTable.roleId]],
                                email = it[mentor[UserTable.email]],
                                number = it[mentor[UserTable.number]],
                                createdAt = it[mentor[UserTable.createdAt]],
                                modifiedAt = it[mentor[UserTable.modifiedAt]]
                            ),
                            user = it.toUser()
                        )
                    )
                )
            } ?: return@post respondGeneral(
                success = false,
                message = "Unknown Error",
                code = HttpStatusCode.InternalServerError
            )
        }
    }
}