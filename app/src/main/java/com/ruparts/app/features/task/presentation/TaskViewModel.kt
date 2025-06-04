package com.ruparts.app.features.task.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruparts.app.core.task.library.TaskLibraryInteractor
import com.ruparts.app.features.task.data.repository.TaskRepository
import com.ruparts.app.features.task.data.repository.TaskUpdateException
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.task.presentation.model.TaskUiEffect
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val taskLibraryInteractor: TaskLibraryInteractor,
) : ViewModel() {

    private val initialTask: TaskListItem = requireNotNull(savedStateHandle["task"])

    private val taskState = MutableStateFlow<TaskListItem>(initialTask)
    private val loadingState = MutableStateFlow<Boolean>(false)

    val screenState = combine(
        taskState,
        loadingState,
        flow { emit(taskLibraryInteractor.getImplementers()) },
        ::Triple,
    ).map { (task, isLoading, implementers) ->
        TaskScreenState(
            task = task,
            isLoading = isLoading,
            implementers = implementers,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TaskScreenState(task = taskState.value),
    )

    private val _uiEffect = MutableSharedFlow<TaskUiEffect>()
    val uiEffect: SharedFlow<TaskUiEffect> = _uiEffect.asSharedFlow()

    fun setTaskDescription(text: String) {
        if (loadingState.value) return

        taskState.update { task ->
            task.copy(description = text)
        }
    }

    fun setTaskImplementer(implementer: String?) {
        if (loadingState.value) return

        taskState.update { task ->
            task.copy(implementer = implementer)
        }
    }

    fun setTaskPriority(priority: TaskPriority) {
        if (loadingState.value) return

        taskState.update { task ->
            task.copy(priority = priority)
        }
    }

    fun setFinishAtDate(finishAtDate: LocalDate?) {
        if (loadingState.value) return

        taskState.update { task ->
            task.copy(finishAtDate = finishAtDate)
        }
    }

    fun updateTask() {
        if (loadingState.value) return

        viewModelScope.launch {
            loadingState.value = true
            taskRepository.updateTask(screenState.value.task).fold(
                onSuccess = { updatedTask ->
                    taskState.value = updatedTask
                    loadingState.value = false
                    _uiEffect.emit(TaskUiEffect.TaskUpdateSuccess)
                },
                onFailure = { exception ->
                    loadingState.value = false
                    when (exception) {
                        is TaskUpdateException -> {
                            _uiEffect.emit(TaskUiEffect.ValidationError(exception.errorMessages))
                        }

                        else -> {
                            _uiEffect.emit(TaskUiEffect.TaskUpdateError(exception.message))
                        }
                    }
                }
            )
        }
    }

    fun changeTaskStatus(newStatus: TaskStatus) {
        if (loadingState.value) return

        viewModelScope.launch {
            loadingState.value = true
            taskRepository.changeTaskStatus(screenState.value.task.id, newStatus).fold(
                onSuccess = { updatedTask ->
                    taskState.value = updatedTask
                    loadingState.value = false
                    _uiEffect.emit(TaskUiEffect.TaskUpdateSuccess)
                },
                onFailure = { exception ->
                    loadingState.value = false
                    when (exception) {
                        is TaskUpdateException -> {
                            _uiEffect.emit(TaskUiEffect.ValidationError(exception.errorMessages))
                        }

                        else -> {
                            _uiEffect.emit(TaskUiEffect.TaskUpdateError(exception.message))
                        }
                    }
                }
            )
        }
    }
}
