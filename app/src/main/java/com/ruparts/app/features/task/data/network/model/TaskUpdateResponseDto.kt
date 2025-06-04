package com.ruparts.app.features.task.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.taskslist.data.network.model.TaskDto

class TaskUpdateResponseDto(
    @SerializedName("type")
    val type: Int,
    @SerializedName("data")
    val data: TaskDto? = null,
    @SerializedName("error")
    val error: ErrorResponseDto? = null,
)

class ErrorResponseDto(
    @SerializedName("data")
    val data: List<ErrorItemDto>? = null,
)

class ErrorItemDto(
    @SerializedName("title")
    val title: String,
)