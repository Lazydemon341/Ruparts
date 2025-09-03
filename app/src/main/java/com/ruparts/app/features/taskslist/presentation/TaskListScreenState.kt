package com.ruparts.app.features.taskslist.presentation

import androidx.compose.runtime.Immutable
import com.ruparts.app.features.taskslist.model.TaskListItem

@Immutable
data class TaskListScreenState(
    val selectedTab: TaskListScreenTab,
    val list: List<TaskListItem>,
)

enum class TaskListScreenTab {
    ALL,
    IN_WORK,
    DONE
}