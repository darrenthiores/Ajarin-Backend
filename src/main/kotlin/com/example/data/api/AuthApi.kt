package com.example.data.api

import com.example.data.table.UserTable
import com.example.model.base.BaseResponse
import com.example.model.base.MetaResponse
import com.example.model.request.authentication.LoginRequest
import com.example.model.request.authentication.RegisterRequest
import com.example.model.response.authentication.AuthResponse
import com.example.util.manager.PasswordManager
import com.example.util.manager.TokenManager
import com.example.util.query.dbQuery
import com.example.util.response.respondGeneral
import com.example.util.validation.isValidEmail
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object AuthApi: KoinComponent {
    private val userTable by inject<UserTable>()
    private val passwordManager by inject<PasswordManager>()

    fun Route.register(path: String) {
        post(path) {
            val request = call.receive<RegisterRequest>()

            when {
                !isValidEmail(request.email) -> {
                    respondGeneral(
                        success = false,
                        message = "Invalid email address",
                        code = HttpStatusCode.BadRequest
                    )

                    return@post
                }
                request.password.length < 7 -> {
                    respondGeneral(
                        success = false,
                        message = "Password length at least 8",
                        code = HttpStatusCode.BadRequest
                    )

                    return@post
                }
                !request.password.contains("[0-9]".toRegex()) -> {
                    respondGeneral(
                        success = false,
                        message = "Password should contains number",
                        code = HttpStatusCode.BadRequest
                    )

                    return@post
                }
                !request.password.contains("[A-Z]".toRegex()) -> {
                    respondGeneral(
                        success = false,
                        message = "Password should contains uppercase",
                        code = HttpStatusCode.BadRequest
                    )

                    return@post
                }
                !request.password.contains("[a-z]".toRegex()) -> {
                    respondGeneral(
                        success = false,
                        message = "Password should contains lowercase",
                        code = HttpStatusCode.BadRequest
                    )

                    return@post
                }
                !request.password.contains("[!\"#$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex()) -> {
                    respondGeneral(
                        success = false,
                        message = "Password should contains special character",
                        code = HttpStatusCode.BadRequest
                    )

                    return@post
                }
            }

            // check email availability
            dbQuery {
                userTable
                    .select {
                        userTable.email eq request.email
                    }
                    .count()
            }.let {
                if (it > 0) {
                    respondGeneral(
                        success = false,
                        message = "Email already registered",
                        code = HttpStatusCode.Conflict
                    )

                    return@post
                }
            }

            // register
            val hashedPw = passwordManager.hashPassword(request.password)

            dbQuery {
                userTable.insert {
                    it[username] = request.username
                    it[roleId] = 1
                    it[email] = request.email
                    it[password] = hashedPw
                    it[number] = request.number
                    it[createdAt] = System.currentTimeMillis()
                    it[modifiedAt] = System.currentTimeMillis()
                }[UserTable.id]
            }.let { id ->
                val token = TokenManager
                    .generateToken(id.toString())
                call.respond(
                    HttpStatusCode.OK,
                    BaseResponse(
                        meta = MetaResponse(
                            success = true,
                            message = "Register Success"
                        ),
                        data = AuthResponse(
                            token = token
                        )
                    )
                )
            }
        }
    }

    fun Route.login(path: String) {
        post(path) {
            val request = call.receive<LoginRequest>()

            // login
            dbQuery {
                userTable.select {
                    userTable.email eq request.email
                }.singleOrNull()
            }?.let {
                if (
                    passwordManager.checkPassword(
                        password = request.password,
                        hashedPassword = it[userTable.password]
                    )
                ) {
                    val token = TokenManager
                        .generateToken(
                            it[userTable.id].toString()
                        )

                    call.respond(
                        HttpStatusCode.OK,
                        BaseResponse(
                            meta = MetaResponse(
                                success = true,
                                message = "Login Success"
                            ),
                            data = AuthResponse(
                                token = token
                            )
                        )
                    )
                } else {
                    respondGeneral(
                        success = false,
                        message = "Password incorrect",
                        code = HttpStatusCode.BadRequest
                    )
                }
            } ?: return@post respondGeneral(
                success = false,
                message = "Password incorrect",
                code = HttpStatusCode.BadRequest
            )
        }
    }
}