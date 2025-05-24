package com.ruparts.app.features.task.presentation.model

sealed interface TaskUiEffect {
    data object TaskUpdateSuccess : TaskUiEffect
    data object TaskUpdateError : TaskUiEffect
}
