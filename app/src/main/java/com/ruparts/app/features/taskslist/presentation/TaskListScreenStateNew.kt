package com.ruparts.app.features.taskslist.presentation

import androidx.compose.runtime.Immutable
import com.ruparts.app.features.taskslist.model.TaskListItem

@Immutable
data class TaskListScreenStateNew(
    val selectedTab: TaskListScreenTab,
    val list: List<TaskListItem>,
    val isLoading: Boolean = false
)

enum class TaskListScreenTab {
    ALL,
    IN_WORK,
    DONE
}