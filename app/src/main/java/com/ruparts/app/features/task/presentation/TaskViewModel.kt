package com.ruparts.app.features.task.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.task.data.repository.TaskRepository
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.taskslist.model.TaskImplementer
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
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

    fun setFinishAtDate(date: LocalDate) {
        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(finishAtDate = date)
            screenState.copy(task = newTask)
        }
    }

    fun updateTask() {
        viewModelScope.launch {
            taskRepository.updateTask(screenState.value.task)
        }
    }
}