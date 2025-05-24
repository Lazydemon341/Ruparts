package com.ruparts.app.features.taskslist.data.network.model

import com.google.gson.annotations.SerializedName
import com.ruparts.app.features.taskslist.model.TaskPriority

enum class TaskPriorityDto(val value: String) {
    @SerializedName("high")
    HIGH("high"),

    @SerializedName("medium")
    MEDIUM("medium"),

    @SerializedName("low")
    LOW("low")
}

fun TaskPriority.toDto(): TaskPriorityDto? {
    return when (this) {
        TaskPriority.HIGH -> TaskPriorityDto.HIGH
        TaskPriority.MEDIUM -> TaskPriorityDto.MEDIUM
        TaskPriority.LOW -> TaskPriorityDto.LOW
    }
}