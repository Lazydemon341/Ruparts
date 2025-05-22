package com.ruparts.app.features.task.presentation.model

import com.ruparts.app.features.taskslist.model.TaskListItem

data class TaskScreenState(
    val title: String,
    val task: TaskListItem,
)