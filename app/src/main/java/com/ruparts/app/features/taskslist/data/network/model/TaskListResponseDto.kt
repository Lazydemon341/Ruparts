package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName

class TaskListResponseDto(
    @SerializedName("type")
    val type: Int,
    @SerializedName("id")
    val id: String,
    @SerializedName("data")
    val data: TaskListResponseDataDto,
)

class TaskListResponseDataDto(
    @SerializedName("list")
    val list: List<TaskDto>,
)

class TaskDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("author")
    val author: AuthorDto?,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("status")
    val status: TaskStatusDto,
    @SerializedName("priority")
    val priority: TaskPriorityDto,
    @SerializedName("implementer")
    val implementer: TaskImplementerDto?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("finish_at")
    val finishAt: String?,
    @SerializedName("created_at_diff")
    val createdAtDiff: String
)

class AuthorDto(
    @SerializedName("id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("username")
    val username: String
)

enum class TaskStatusDto(val value: String) {
    @SerializedName("to_do")
    TO_DO("to_do"),

    @SerializedName("in_progress")
    IN_PROGRESS("in_progress"),

    @SerializedName("completed")
    COMPLETED("completed"),

    @SerializedName("canceled")
    CANCELLED("canceled")
}

enum class TaskPriorityDto(val value: String) {
    @SerializedName("high")
    HIGH("high"),

    @SerializedName("medium")
    MEDIUM("medium"),

    @SerializedName("low")
    LOW("low")
}

enum class TaskImplementerDto {
    @SerializedName("ROLE_USER")
    USER,

    @SerializedName("ROLE_PurchasesManager")
    PURCHASES_MANAGER,

    @SerializedName("ROLE_Storekeeper")
    STOREKEEPER
}