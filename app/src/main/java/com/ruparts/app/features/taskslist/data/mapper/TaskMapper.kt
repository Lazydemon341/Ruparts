package com.ruparts.app.features.taskslist.data.mapper

import com.ruparts.app.core.utils.toLocalDate
import com.ruparts.app.features.taskslist.data.network.model.TaskDto
import com.ruparts.app.features.taskslist.data.network.model.toDomain
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class TaskMapper @Inject constructor() {

    private val dateFormatterLazy = lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx")
    }

    fun mapTasks(list: List<TaskDto>): List<TaskListGroup> {
        val dateFormatter = dateFormatterLazy.value
        return list
            .groupBy { it.type }
            .map { (type, tasks) ->
                TaskListGroup(
                    title = type.toDomain().displayName,
                    tasks = tasks.map { task ->
                        mapTask(task, dateFormatter)
                    }
                )
            }
    }

    fun mapTask(task: TaskDto): TaskListItem {
        return mapTask(task, dateFormatterLazy.value)
    }

    private fun mapTask(task: TaskDto, dateFormatter: DateTimeFormatter): TaskListItem {
        return TaskListItem(
            id = task.id,
            title = task.title,
            description = task.description,
            status = task.status.toDomain(),
            priority = task.priority.toDomain(),
            implementer = task.implementer,
            type = task.type.toDomain(),
            createdAtDate = task.createdAt.toLocalDate(dateFormatter),
            finishAtDate = task.finishAt.toLocalDate(dateFormatter),
            updatedAtDate = task.updatedAt.toLocalDate(dateFormatter),
        )
    }
}