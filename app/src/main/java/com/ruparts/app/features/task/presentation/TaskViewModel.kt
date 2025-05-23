package com.ruparts.app.features.task.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.taskslist.model.TaskImplementer
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _screenState = MutableStateFlow(
        TaskScreenState(
            task = requireNotNull(savedStateHandle[TaskFragment.ARG_TASK_KEY])
        )
    )
    val screenState = _screenState.asStateFlow()

    fun setTaskDescription(text: String) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(description = text)
            screenState.copy(task = newTask)
        }
    }

    fun setTaskImplementer(implementer: TaskImplementer) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(implementer = implementer)
            screenState.copy(task = newTask)
        }
    }

    fun setTaskPriority(priority: TaskPriority) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(priority = priority)
            screenState.copy(task = newTask)
        }
    }

    fun setTask(item: TaskListItem) {
        _screenState.update { screenState ->
            screenState.copy(task = item)
        }
    }

    fun setFinishAtDate(item: String) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(finishAtDate = item)
            screenState.copy(task = newTask)
        }
    }
}