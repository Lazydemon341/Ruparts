package com.ruparts.app.features.taskslist.model

import java.io.Serializable

data class TaskListItem(
    val id: Long,
    val status: TaskStatus,
    val priority: TaskPriority,
    val title: String,
    val description: String,
    val date: String?,
    val implementer: String?,
    val finishAtDate: String?
) : Serializable