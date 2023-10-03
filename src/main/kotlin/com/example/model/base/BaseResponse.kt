package com.example.model.base

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse <T> (
    val meta: MetaResponse,
    val data: T?
)