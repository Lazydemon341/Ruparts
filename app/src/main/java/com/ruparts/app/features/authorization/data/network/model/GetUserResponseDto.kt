package com.ruparts.app.features.authorization.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.model.User
import java.time.LocalDate

class GetUserResponseDto (
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("username")
    val userName: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("roles")
    val roles: List<String>,
    @SerializedName("created_at")
    val createdAt: LocalDate,
    @SerializedName("updated_at")
    val updatedAt: LocalDate,
    ) {

    fun mapToUser(): User =
        User(uuid, userName, displayName, isActive, roles, createdAt, updatedAt)
}