package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName

class TaskListResponseDto(
    @SerializedName("data")
    val data: TaskListResponseDataDto,
)

class TaskListResponseDataDto(
    @SerializedName("list")
    val list: List<TaskDto>,
)

