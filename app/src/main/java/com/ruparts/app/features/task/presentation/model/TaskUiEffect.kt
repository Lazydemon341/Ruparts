package com.ruparts.app.features.task.presentation.model

sealed interface TaskUiEffect {
    data object TaskUpdateSuccess : TaskUiEffect
    data class TaskUpdateError(val errorMessages: List<String> = emptyList()) : TaskUiEffect
}
