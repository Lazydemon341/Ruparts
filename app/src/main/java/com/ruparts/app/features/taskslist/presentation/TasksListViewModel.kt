package com.ruparts.app.features.taskslist.presentation

import androidx.lifecycle.ViewModel
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.presentation.model.TasksListScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TasksListViewModel @Inject constructor() : ViewModel() {

    private val _screenState = MutableStateFlow(mockScreenState)
    val screenState = _screenState.asStateFlow()

    fun filterTasks(query: String) {

        val resultList = mutableListOf<TaskListGroup>()

        for (group in mockTasksList) {
            val filtered: List<TaskListItem> = group.tasks.filter { it.title.containsNormalized(query) }
            if (filtered.isNotEmpty()) {
                resultList.add(group.copy(tasks = filtered))
            }
        }
        _screenState.update { it.copy(groups = resultList) }
    }

    fun filterTasks(status: TaskStatus?) {
        val resultList = mutableListOf<TaskListGroup>()

        for (group in mockTasksList) {
            val filtered: List<TaskListItem> = group.tasks.filter { it.status == status }
            if (filtered.isNotEmpty()) {
                resultList.add(group.copy(tasks = filtered))
            }
        }
        _screenState.update { it.copy(groups = resultList) }
    }

    fun showAllTasks() {
        val resultList = mutableListOf<TaskListGroup>()
        for (group in mockTasksList) {
            resultList.add(group.copy(tasks = group.tasks))
        }
        _screenState.update { it.copy(groups = resultList) }
    }

    fun String.containsNormalized(value: String): Boolean {
        fun normalize(input: String): String {
            return input.lowercase().replace('ё', 'е')
        }

        return normalize(this).contains(normalize(value))
    }
}

private val mockTasksList = listOf(
    TaskListGroup(
        title = "Приёмка груза от поставщика",
        tasks = listOf(
            TaskListItem(
                id = 0,
                status = TaskStatus.IN_PROGRESS,
                priority = TaskPriority.HIGH,
                title = "Приёмка груза от МаксимумСПБ",
                description = "Номер заказа: 3321\nДоставка: ТК деловые линии",
                date = "10 июн 23"
            )
        ),
        id = 1
    ),
    TaskListGroup(
        title = "Сборка заказа",
        tasks = listOf(
            TaskListItem(
                id = 1,
                status = TaskStatus.TODO,
                priority = TaskPriority.LOW,
                title = "Сборка заказа для Автопитер",
                description = "Номер заказа: 3512\nСумма: 34512 руб., кол-во позиций: 35",
                date = "9 июн 23"
            )
        ),
        id = 2
    ),
    TaskListGroup(
        title = "Сборка возврата",
        tasks = listOf(
            TaskListItem(
                id = 2,
                status = TaskStatus.TODO,
                priority = TaskPriority.MEDIUM,
                title = "Сборка возврата 3301",
                description = "Клиент: ООО Лидер",
                date = "9 июн 23"
            )
        ),
        id = 3
    )
)

private val mockScreenState = TasksListScreenState(
    title = "Задачи",
    groups = mockTasksList,
)
