package com.ruparts.app.features.taskslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.taskslist.data.repository.TaskListRepository
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.presentation.model.TaskListScreenEffect
import com.ruparts.app.features.taskslist.presentation.model.TaskListScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksListViewModelNew @Inject constructor(
    private val repository: TaskListRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val tabs = MutableStateFlow<TaskListScreenTab>(TaskListScreenTab.ALL)
    private val tasks = MutableStateFlow<List<TaskListItem>>(emptyList())
    private val isLoading = MutableStateFlow(false)

    private var loadTasksJob: Job? = null

    private val _effect = MutableSharedFlow<TaskListScreenEffect>()
    val effect = _effect.asSharedFlow()

    @OptIn(FlowPreview::class)
    val state: StateFlow<TaskListScreenStateNew> = combine(
        tasks,
        tabs,
        searchQuery.debounce(300L),
        isLoading,
    ) { taskList, tab, query, loading ->
        TaskListScreenStateNew(
            list = performFilter(taskList, tab, query),
            selectedTab = tab,
            isLoading = loading
        )
    }.onStart { loadTasks() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialScreenState,
        )

    fun refreshTasks() {
        loadTasks()
    }

    private fun loadTasks() {
        loadTasksJob?.cancel()
        loadTasksJob = viewModelScope.launch {
            isLoading.value = true
            tasks.value = repository.getTaskList()
                .getOrDefault(emptyList())
            isLoading.value = false
        }
    }

    private fun performFilter(
        taskList: List<TaskListItem>,
        tab: TaskListScreenTab,
        query: String
    ): List<TaskListItem> {
        if (tab == TaskListScreenTab.ALL && query.isEmpty()) {
            return taskList
        }
        return taskList.filter { task ->
            val statusFits = when (tab) {
                TaskListScreenTab.ALL -> true
                TaskListScreenTab.DONE -> task.status == TaskStatus.COMPLETED
                TaskListScreenTab.IN_WORK -> task.status == TaskStatus.IN_PROGRESS
            }
            return@filter statusFits
                && (task.title.containsNormalized(query) || task.description.containsNormalized(query))
        }
    }

    private fun String.containsNormalized(value: String): Boolean {
        fun normalize(input: String): String {
            return input.lowercase().replace('ё', 'е')
        }
        return normalize(this).contains(normalize(value))
    }

    fun handleEvent(event: TaskListScreenEvent) {
        when (event) {
            is TaskListScreenEvent.OnTabClick -> {
                if (!state.value.isLoading) {
                    tabs.value = event.tab
                }
            }

            is TaskListScreenEvent.OnItemClick -> sendEffect(TaskListScreenEffect.NavigateToItemDetails(event.item))
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    private fun sendEffect(effect: TaskListScreenEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}

private val initialScreenState = TaskListScreenStateNew(
    list = emptyList(),
    selectedTab = TaskListScreenTab.ALL,
    isLoading = true
)