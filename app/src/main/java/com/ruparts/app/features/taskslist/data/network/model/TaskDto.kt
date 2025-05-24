package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName

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
    val createdAt: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("finish_at")
    val finishAt: String?,
    @SerializedName("created_at_diff")
    val createdAtDiff: String
)