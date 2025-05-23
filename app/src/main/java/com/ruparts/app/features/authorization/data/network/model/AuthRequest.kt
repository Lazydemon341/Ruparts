package com.ruparts.app.features.authorization.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Request model for login by code API
 */
data class AuthRequest(
    @SerializedName("code")
    val code: String
)
