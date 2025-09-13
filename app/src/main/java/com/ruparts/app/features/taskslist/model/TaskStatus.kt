package com.ruparts.app.features.taskslist.model

enum class TaskStatus(val textEquivalent: String) {
    TODO(""),
    IN_PROGRESS("В работе"),
    COMPLETED("Готово"),
    CANCELLED("На паузе")

}