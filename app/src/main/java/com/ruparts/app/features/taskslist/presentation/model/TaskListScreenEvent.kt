package com.ruparts.app.features.taskslist.presentation.model

import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.presentation.TaskListScreenTab

sealed interface TaskListScreenEvent {
    data class OnTabClick(val tab:TaskListScreenTab) : TaskListScreenEvent
    data class OnItemClick(val item: TaskListItem) : TaskListScreenEvent
}