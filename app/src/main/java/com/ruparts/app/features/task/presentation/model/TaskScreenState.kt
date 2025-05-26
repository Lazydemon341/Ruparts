package com.ruparts.app.features.task.presentation.model

import com.ruparts.app.features.taskslist.model.TaskListItem

data class TaskScreenState(
    val task: TaskListItem,
    val isLoading: Boolean = false
)