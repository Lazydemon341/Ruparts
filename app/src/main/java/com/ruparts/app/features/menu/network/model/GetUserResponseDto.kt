package com.ruparts.app.features.menu.network.model

import com.google.gson.annotations.SerializedName

class GetUserResponseDto (
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("display_name")
    val display_name: String,
    @SerializedName("is_active")
    val is_active: Boolean,
    @SerializedName("roles")
    val roles: List<String>,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String,
    )