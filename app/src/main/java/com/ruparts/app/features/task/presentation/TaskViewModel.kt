package com.ruparts.app.features.task.presentation

import androidx.lifecycle.ViewModel
import com.ruparts.app.R
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import com.ruparts.app.features.taskslist.presentation.mockScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor() : ViewModel(){

//    description.text = mockTask.description // пишет, что неверный тип, ждет editable
//
//    when (mockTask.priority) { //это
//        TaskPriority.HIGH -> {
//            priorityImage.setImageResource(R.drawable.arrow_up)
//            priority.text = "Высокий"
//        }
//        TaskPriority.LOW -> {
//            priorityImage.setImageResource(R.drawable.arrow_down)
//            priority.text = "Низкий"
//        }
//        TaskPriority.MEDIUM -> {
//            priorityImage.setImageResource(R.drawable.equal)
//            priority.text = "Средний"
//        }
//    }
//    createdDate.text = mockTask.date

}

private val mockTask = TaskListItem(
    id = 0,
    status = TaskStatus.IN_PROGRESS,
    priority = TaskPriority.HIGH,
    title = "Приёмка груза от МаксимумСПБ",
    description = "Номер заказа: 3321\nДоставка: ТК деловые линии",
    date = "10 июн 23"
)