package com.example.util.response

import com.example.model.base.BaseResponse
import com.example.model.base.MetaResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

suspend fun PipelineContext<Unit, ApplicationCall>.respondGeneral(
    success:Boolean,
    message:String,
    code: HttpStatusCode
) {
    call.respond(
        code,
        BaseResponse(
            meta = MetaResponse(
                success = success,
                message = message
            ),
            data = null
        )
    )
}