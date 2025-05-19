package com.ruparts.app.features.task.presentation

import androidx.lifecycle.ViewModel
import com.ruparts.app.features.task.presentation.model.TaskScreenState
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor() : ViewModel(){

    val screenState = mockScreenState //??

}

private val mockTask = TaskListItem(
    id = 0,
    status = TaskStatus.IN_PROGRESS,
    priority = TaskPriority.HIGH,
    title = "Приёмка груза от МаксимумСПБ",
    description = "Номер заказа: 3321\nДоставка: ТК деловые линии",
    date = "10 июн 23"
)

private val mockScreenState = TaskScreenState(
    title = "Задача",
    task = mockTask,
)