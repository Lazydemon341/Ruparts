package com.ruparts.app.features.task.presentation

import androidx.lifecycle.ViewModel
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor() : ViewModel(){

    private val _screenState = MutableStateFlow(mockScreenState)
    val screenState = _screenState.asStateFlow()

    fun setTaskDescription (text: String) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(description = text)
            screenState.copy(task = newTask)
        }
    }

    fun setTaskImplementer (text: String) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(implementer = text)
            screenState.copy(task = newTask)
        }
    }

    fun setTaskPriority (priority: TaskPriority) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(priority = priority)
            screenState.copy(task = newTask)
        }
    }

    fun setTask (item: TaskListItem) {
        _screenState.update { screenState ->
            screenState.copy(task = item)
        }
    }

    fun setFinishAtDate (item: String) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(date = item)
            screenState.copy(task = newTask)
        }
    }
}

private val mockTask = TaskListItem(
    id = 0,
    status = TaskStatus.IN_PROGRESS,
    priority = TaskPriority.HIGH,
    title = "Приёмка груза от МаксимумСПБ",
    description = "Номер заказа: 3321\nДоставка: ТК деловые линии",
    date = "10 июн 23",
    implementer = "Кладовщик",
    finishAtDate =""
)

private val mockScreenState = TaskScreenState(
    title = "Задача",
    task = mockTask,
)