package com.ruparts.app.features.authorization.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for login by code API
 */
data class AuthResponse(
    @SerializedName("token") val token: String
)
