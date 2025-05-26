package com.ruparts.app.features.task.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.taskslist.data.network.model.TaskDto

class TaskUpdateResponseDto(
    @SerializedName("data")
    val data: TaskDto,
)