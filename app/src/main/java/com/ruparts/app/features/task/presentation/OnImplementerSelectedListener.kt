package com.ruparts.app.features.task.presentation

import com.ruparts.app.features.taskslist.model.TaskImplementer

interface OnImplementerSelectedListener {
    fun onItemSelected(implementer: TaskImplementer)
}