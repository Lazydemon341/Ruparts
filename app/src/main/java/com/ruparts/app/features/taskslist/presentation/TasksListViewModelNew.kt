package com.ruparts.app.features.taskslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEffect
import com.ruparts.app.features.assembly.presentation.model.AssemblyScreenEvent
import com.ruparts.app.features.taskslist.data.repository.TaskListRepository
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.model.TaskType
import com.ruparts.app.features.taskslist.presentation.model.TaskListScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
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
            scope = viewModelScope + Dispatchers.Default,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = initialScreenState,
        )

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

        return taskList.filter { task ->
            if (tab == TaskListScreenTab.ALL && query.isEmpty()) {
                return taskList
            }
            when (tab) {
                TaskListScreenTab.ALL -> true
                TaskListScreenTab.DONE -> task.status == TaskStatus.CANCELLED || task.status == TaskStatus.COMPLETED
                TaskListScreenTab.IN_WORK -> task.status == TaskStatus.TODO
            }
            task.title.containsNormalized(query)
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
            is TaskListScreenEvent.OnTabClick -> Unit//onTabClick(event.tab)
            is TaskListScreenEvent.OnItemClick -> Unit//sendEffect(AssemblyScreenEffect.NavigateToItemDetails(event.item))
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

}

private val initialScreenState = TaskListScreenStateNew(
    list = emptyList(),
    selectedTab = TaskListScreenTab.ALL,
    isLoading = true
)