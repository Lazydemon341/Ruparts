package com.ruparts.app.features.authorization.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Request model for login by code API
 */
@JsonClass(generateAdapter = true)
data class AuthRequest(
    @Json(name = "code")
    val code: String,
)
