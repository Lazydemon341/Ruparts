package com.ruparts.app.features.authorization.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.core.model.User
import com.ruparts.app.core.utils.toLocalDate
import java.time.format.DateTimeFormatter

class GetUserResponseDto(
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
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
) {

    fun mapToUser(dateTimeFormatter: DateTimeFormatter): User {
        return User(
            uuid = uuid,
            userName = userName,
            displayName = displayName,
            isActive = isActive,
            roles = roles,
            createdAt = createdAt.toLocalDate(dateTimeFormatter),
            updatedAt = updatedAt.toLocalDate(dateTimeFormatter),
        )
    }
}