package com.ruparts.app.features.task.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.features.task.data.repository.TaskRepository
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.task.presentation.model.TaskUiEffect
import com.ruparts.app.features.taskslist.model.TaskImplementer
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _uiEffect = MutableSharedFlow<TaskUiEffect>()
    val uiEffect: SharedFlow<TaskUiEffect> = _uiEffect.asSharedFlow()

    fun setTaskDescription(text: String) {
        if (_screenState.value.isLoading) return

        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(description = text)
            screenState.copy(task = newTask)
        }
    }

    fun setTaskImplementer(implementer: TaskImplementer) {
        if (_screenState.value.isLoading) return

        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(implementer = implementer)
            screenState.copy(task = newTask)
        }
    }

    fun setTaskPriority(priority: TaskPriority) {
        if (_screenState.value.isLoading) return

        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(priority = priority)
            screenState.copy(task = newTask)
        }
    }

    fun setTask(item: TaskListItem) {
        if (_screenState.value.isLoading) return

        _screenState.update { screenState ->
            screenState.copy(task = item)
        }
    }

    fun setFinishAtDate(date: LocalDate) {
        if (_screenState.value.isLoading) return

        _screenState.update { screenState ->
            val task = screenState.task
            val newTask = task.copy(finishAtDate = date)
            screenState.copy(task = newTask)
        }
    }

    fun updateTask() {
        if (_screenState.value.isLoading) return

        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            taskRepository.updateTask(screenState.value.task).fold(
                onSuccess = { updatedTask ->
                    _screenState.update {
                        it.copy(
                            task = updatedTask,
                            isLoading = false,
                        )
                    }
                    _uiEffect.emit(TaskUiEffect.TaskUpdateSuccess)
                },
                onFailure = {
                    _screenState.update { it.copy(isLoading = false) }
                    _uiEffect.emit(TaskUiEffect.TaskUpdateError)
                }
            )
        }
    }

    fun changeTaskStatus(newStatus: TaskStatus) {
        if (_screenState.value.isLoading) return

        viewModelScope.launch {
            _screenState.update { it.copy(isLoading = true) }
            taskRepository.changeTaskStatus(screenState.value.task.id, newStatus).fold(
                onSuccess = { updatedTask ->
                    _screenState.update {
                        it.copy(
                            task = updatedTask,
                            isLoading = false,
                        )
                    }
                    _uiEffect.emit(TaskUiEffect.TaskUpdateSuccess)
                },
                onFailure = {
                    _screenState.update { it.copy(isLoading = false) }
                    _uiEffect.emit(TaskUiEffect.TaskUpdateError)
                }
            )
        }
    }

}