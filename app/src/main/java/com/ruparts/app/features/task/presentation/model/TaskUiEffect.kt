package com.ruparts.app.features.task.presentation.model

sealed interface TaskUiEffect {
    data object TaskUpdateSuccess : TaskUiEffect
    data class ValidationError(val errorMessages: Map<String, String>) : TaskUiEffect
    data class TaskUpdateError(val errorMessage: String?) : TaskUiEffect
}
