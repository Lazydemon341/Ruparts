package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.taskslist.model.TaskStatus

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

fun TaskStatus.toDto(): TaskStatusDto? {
    return when (this) {
        TaskStatus.TODO -> TaskStatusDto.TO_DO
        TaskStatus.IN_PROGRESS -> TaskStatusDto.IN_PROGRESS
        TaskStatus.COMPLETED -> TaskStatusDto.COMPLETED
        TaskStatus.CANCELLED -> TaskStatusDto.CANCELLED
    }
}