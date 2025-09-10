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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

@HiltViewModel
class TasksListViewModelNew @Inject constructor(
    private val repository: TaskListRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<TaskListScreenStateNew>(
        TaskListScreenStateNew(
            selectedTab = TaskListScreenTab.ALL,
            list = listOf(
                TaskListItem(
                    id = 1,
                    status = TaskStatus.TODO,
                    priority = TaskPriority.LOW,
                    title = "Заголовок задачи",
                    description = "Описание задачи",
                    implementer = "Кладовщик",
                    type = TaskType.CUSTOM,
                    createdAtDate = null,
                    finishAtDate = null,
                    updatedAtDate = null,
                )
            )
        )
    )
    val state = _state.asStateFlow()

    private val tabs = MutableStateFlow<TaskListScreenTab>(TaskListScreenTab.ALL)
    private val tasks = MutableStateFlow<List<TaskListItem>>(emptyList())

    private var loadTasksJob: Job? = null

    val screenState: StateFlow<TaskListScreenStateNew> = combine(
        tasks,
        tabs
        ) { taskList, tab ->
        TaskListScreenStateNew(
            list = taskList,
            selectedTab = tab
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
            // isLoading.value = true
            tasks.value = repository.getTaskList()
                .getOrDefault(emptyList())
            // isLoading.value = false
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

        // val resultList = mutableListOf<TaskListGroup>()
        // for (group in taskList) {
        //     val filtered = group.tasks.filter {
        //         (status == null || it.status == status)
        //             && it.title.containsNormalized(query)
        //     }
        //     if (filtered.isNotEmpty()) {
        //         resultList.add(group.copy(tasks = filtered))
        //     }
        // }

        return taskList.filter { task ->
            when (tab) {
                TaskListScreenTab.ALL -> true
                TaskListScreenTab.DONE -> task.status == TaskStatus.CANCELLED || task.status == TaskStatus.COMPLETED
                TaskListScreenTab.IN_WORK -> task.status == TaskStatus.TODO
            }
        }
    }

    fun handleEvent(event: AssemblyScreenEvent) {
        when (event) {
            is TaskListScreenEvent.OnTabClick -> onTabClick(event.tab)
            is TaskListScreenEvent.OnItemClick -> sendEffect(AssemblyScreenEffect.NavigateToItemDetails(event.item))
        }
    }




}

private val initialScreenState = TaskListScreenStateNew(
    list = emptyList(),
    selectedTab = TaskListScreenTab.ALL
)