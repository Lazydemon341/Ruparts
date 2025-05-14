package com.ruparts.app.features.taskslist.model

data class TaskListItem(
    val id: Long,
    val status: TaskStatus,
    val priority: TaskPriority,
    val title: String,
    val description: String,
    val date: String,
)