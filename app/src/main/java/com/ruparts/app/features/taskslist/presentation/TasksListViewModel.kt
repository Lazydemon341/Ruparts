package com.ruparts.app.features.taskslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.taskslist.data.repository.TaskListRepository
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.presentation.model.TasksListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val repository: TaskListRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val taskStatusFilter = MutableStateFlow<TaskStatus?>(null)

    val screenState: StateFlow<TasksListScreenState> = combine(
        flow { emit(repository.getTaskList()) },
        taskStatusFilter,
        searchQuery.debounce(300L),
    ) { taskList, status, query ->
        TasksListScreenState(
            groups = performFilter(taskList, status, query),
        )
    }.flowOn(Dispatchers.Default)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialScreenState,
        )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onTaskStatusFilterChange(status: TaskStatus?) {
        taskStatusFilter.value = status
    }

    private fun performFilter(
        taskList: List<TaskListGroup>,
        status: TaskStatus?,
        query: String
    ): List<TaskListGroup> {
        if (status == null && query.isEmpty()) {
            return taskList
        }

        val resultList = mutableListOf<TaskListGroup>()
        for (group in taskList) {
            val filtered = group.tasks.filter {
                (status == null || it.status == status)
                        && it.title.containsNormalized(query)
            }
            if (filtered.isNotEmpty()) {
                resultList.add(group.copy(tasks = filtered))
            }
        }
        return resultList
    }

    private fun String.containsNormalized(value: String): Boolean {
        fun normalize(input: String): String {
            return input.lowercase().replace('ё', 'е')
        }
        return normalize(this).contains(normalize(value))
    }
}

private val initialScreenState = TasksListScreenState(
    groups = emptyList(),
)
