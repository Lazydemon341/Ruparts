package com.ruparts.app.features.task.presentation

import com.ruparts.app.features.taskslist.model.TaskPriority

interface OnPrioritySelectedListener {
    fun onPrioritySelected(item: TaskPriority)
}