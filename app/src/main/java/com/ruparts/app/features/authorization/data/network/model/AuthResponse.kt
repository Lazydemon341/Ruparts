package com.ruparts.app.features.authorization.data.network.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Response model for login by code API
 */
@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "token") val token: String
)
