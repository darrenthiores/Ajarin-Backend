package com.example.data.api

import com.example.data.table.UserTable
import com.example.data.table.UserTable.toUser
import com.example.model.base.BaseResponse
import com.example.model.base.MetaResponse
import com.example.model.request.id.IdRequest
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
import org.jetbrains.exposed.sql.select
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.UUID

object UserApi: KoinComponent {
    private val userTable by inject<UserTable>()

    fun Route.getUser(path: String) {
        get(path) {
            val id = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@get call.respondText(
                "Unknown Error",
                status = HttpStatusCode.InternalServerError
            )

            dbQuery {
                userTable.select {
                    userTable.id eq UUID.fromString(id)
                }.singleOrNull()
            }?.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve User Success"
                        ),
                        data = it.toUser()
                    )
                )
            } ?: return@get respondGeneral(
                success = false,
                message = "Unknown Error",
                code = HttpStatusCode.InternalServerError
            )
        }
    }

    fun Route.getUserById(path: String) {
        post(path) {
            val request = call.receive<IdRequest>()

            dbQuery {
                userTable.select {
                    userTable.id eq UUID.fromString(request.id)
                }.singleOrNull()
            }?.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve User Success"
                        ),
                        data = User(
                            id = it[userTable.id].toString(),
                            username = it[userTable.username],
                            roleId = it[userTable.roleId],
                            email = it[userTable.email],
                            number = it[userTable.number],
                            createdAt = it[userTable.createdAt],
                            modifiedAt = it[userTable.modifiedAt]
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