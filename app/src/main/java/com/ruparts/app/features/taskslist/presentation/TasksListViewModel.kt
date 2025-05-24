package com.ruparts.app.features.taskslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.taskslist.data.repository.TaskListRepository
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.presentation.model.TasksListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class TasksListViewModel @Inject constructor(
    private val repository: TaskListRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val taskStatusFilter = MutableStateFlow<TaskStatus?>(null)
    private val isLoading = MutableStateFlow(false)
    private val taskGroups = MutableStateFlow<List<TaskListGroup>>(emptyList())

    private var loadTasksJob: Job? = null

    @OptIn(FlowPreview::class)
    val screenState: StateFlow<TasksListScreenState> = combine(
        taskGroups,
        taskStatusFilter,
        searchQuery.debounce(300L),
        isLoading,
    ) { taskList, status, query, loading ->
        TasksListScreenState(
            groups = performFilter(taskList, status, query),
            isLoading = loading
        )
    }.onStart { loadTasks() }
        .stateIn(
            scope = viewModelScope + Dispatchers.Default,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialScreenState,
        )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onTaskStatusFilterChange(status: TaskStatus?) {
        taskStatusFilter.value = status
    }

    fun refreshTasks() {
        loadTasks()
    }

    private fun loadTasks() {
        loadTasksJob?.cancel()
        loadTasksJob = viewModelScope.launch {
            isLoading.value = true
            taskGroups.value = repository.getTaskList()
                .getOrDefault(emptyList())
            isLoading.value = false
        }
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
    isLoading = true
)
