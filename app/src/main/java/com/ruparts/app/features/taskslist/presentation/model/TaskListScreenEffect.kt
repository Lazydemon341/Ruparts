package com.ruparts.app.features.taskslist.presentation.model

import com.ruparts.app.features.taskslist.model.TaskListItem

sealed interface TaskListScreenEffect {
    data class NavigateToItemDetails(val item: TaskListItem) : TaskListScreenEffect
}