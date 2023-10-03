package com.example.data.api

import com.example.data.table.MentorTable
import com.example.data.table.MentorTable.toMentor
import com.example.data.table.UserTable
import com.example.model.base.BaseResponse
import com.example.model.base.MetaResponse
import com.example.model.request.id.IdRequest
import com.example.model.request.mentor.RegisterMentorRequest
import com.example.model.request.mentor.SearchMentorRequest
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
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.*

object MentorApi: KoinComponent {
    private val userTable by inject<UserTable>()
    private val mentorTable by inject<MentorTable>()

    fun Route.registerAsMentor(path: String) {
        post(path) {
            val request = call.receive<RegisterMentorRequest>()

            dbQuery {
                mentorTable.insert {
                    it[id] = UUID.fromString(request.id)
                    it[education] = request.education
                    it[rating] = 0.0
                    it[ratingCount] = 0
                    it[courses] = request.courses
                        .toString()
                        .replace(" ", "")
                    it[price] = request.price
                    it[priceCategory] = getCategory(request.price) ?: ""
                    it[createdAt] = System.currentTimeMillis()
                    it[modifiedAt] = System.currentTimeMillis()
                }
            }.let {
                dbQuery {
                    userTable.update(
                        { userTable.id eq UUID.fromString(request.id) }
                    ) {
                        it[userTable.roleId] = 2
                    }
                }.let {
                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            meta = MetaResponse(
                                success = true,
                                message = "Registered as Mentor"
                            ),
                            data = null
                        )
                    )
                }
            }
        }
    }

    fun Route.getMentors(path: String) {
        get(path) {
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

            dbQuery {
                mentorTable
                    .join(
                        UserTable,
                        onColumn = MentorTable.id,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .selectAll()
                    .limit(
                        n = pageSize,
                        offset = skip.toLong()
                    )
                    .map {
                        it.toMentor(
                            name = it[userTable.username]
                        )
                    }
            }.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve Mentors Success"
                        ),
                        data = it
                    )
                )
            }
        }
    }

    fun Route.searchMentors(path: String) {
        post(path) {
            val request = call.receive<SearchMentorRequest>()
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
                mentorTable
                    .join(
                        UserTable,
                        onColumn = MentorTable.id,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .select {
                        (if (request.name.isBlank()) Op.TRUE else (userTable.username.like(request.name + "%"))) and
                                (if (request.education.isBlank()) Op.TRUE else (mentorTable.education eq request.education)) and
                                (mentorTable.rating greaterEq request.rating) and
                                (if (request.courseId.isBlank()) Op.TRUE else (mentorTable.courses.like("%${request.courseId}%"))) and
                                (if (request.price.isBlank()) Op.TRUE else (mentorTable.priceCategory eq request.price))
                    }
                    .limit(
                        n = pageSize,
                        offset = skip.toLong()
                    )
                    .map {
                        it.toMentor(
                            name = it[userTable.username]
                        )
                    }
            }.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve Mentors Success"
                        ),
                        data = it
                    )
                )
            }
        }
    }

    fun Route.getMentor(path: String) {
        get(path) {
            val id = call.principal<JWTPrincipal>()?.payload?.getClaim("id")?.asString() ?: return@get call.respondText(
                "Unknown Error",
                status = HttpStatusCode.InternalServerError
            )

            dbQuery {
                mentorTable
                    .join(
                        UserTable,
                        onColumn = MentorTable.id,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .select {
                        mentorTable.id eq UUID.fromString(id)
                    }.singleOrNull()
            }?.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve Mentor Success"
                        ),
                        data = it.toMentor(
                            name = it[userTable.username]
                        )
                    )
                )
            } ?: return@get respondGeneral(
                success = false,
                message = "You are not registered as a mentor",
                code = HttpStatusCode.BadRequest
            )
        }
    }

    fun Route.getMentorById(path: String) {
        post(path) {
            val request = call.receive<IdRequest>()

            dbQuery {
                mentorTable
                    .join(
                        UserTable,
                        onColumn = MentorTable.id,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .select {
                        mentorTable.id eq UUID.fromString(request.id)
                    }.singleOrNull()
            }?.let {
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Retrieve User Success"
                        ),
                        data = it.toMentor(
                            name = it[userTable.username]
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