package com.ruparts.app.features.taskslist.data.mapper

import com.ruparts.app.features.taskslist.data.network.model.TaskDto
import com.ruparts.app.features.taskslist.data.network.model.toDomain
import com.ruparts.app.features.taskslist.model.TaskImplementer
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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
            implementer = task.implementer?.toDomain() ?: TaskImplementer.UNKNOWN,
            type = task.type.toDomain(),
            createdAtDate = mapLocalDate(task.createdAt, dateFormatter),
            finishAtDate = mapLocalDate(task.finishAt, dateFormatter),
            updatedAtDate = mapLocalDate(task.updatedAt, dateFormatter)
        )
    }



    private fun mapLocalDate(
        dateString: String?,
        dateFormatter: DateTimeFormatter
    ): LocalDate? {
        if (dateString == null) return null
        return try {
            if (dateString.contains(" ")) {
                val offsetDateTime = OffsetDateTime.parse(dateString, dateFormatter)
                offsetDateTime.toLocalDate()
            } else {
                LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE)
            }
        } catch (e: DateTimeParseException) {
            null
        }
    }
}