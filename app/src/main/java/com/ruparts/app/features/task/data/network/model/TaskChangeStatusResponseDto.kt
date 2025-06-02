package com.ruparts.app.features.task.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.taskslist.data.network.model.TaskDto

class TaskChangeStatusResponseDto(
    @SerializedName("type")
    val type: Int,
    @SerializedName("data")
    val data: TaskDto? = null,
    @SerializedName("error")
    val error: ErrorResponseDto? = null,
)