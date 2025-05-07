package com.ruparts.app.features.taskslist.presentation.model

import com.ruparts.app.features.taskslist.model.TaskListGroup

data class TasksListScreenState(
    val title: String,
    val groups: List<TaskListGroup>,
)