package com.ruparts.app.features.taskslist.data.mapper

import com.ruparts.app.features.taskslist.data.network.model.TaskDto
import com.ruparts.app.features.taskslist.data.network.model.TaskImplementerDto
import com.ruparts.app.features.taskslist.data.network.model.TaskPriorityDto
import com.ruparts.app.features.taskslist.data.network.model.TaskStatusDto
import com.ruparts.app.features.taskslist.model.TaskImplementer
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

class TaskListMapper @Inject constructor() {

    private val dateFormatterLazy = lazy {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss xxx")
    }

    fun mapTasks(list: List<TaskDto>): List<TaskListGroup> {
        val dateFormatter = dateFormatterLazy.value
        return list
            .groupBy { it.type }
            .map { (type, tasks) ->
                TaskListGroup(
                    title = type,
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
            status = mapTaskStatus(task.status),
            priority = mapTaskPriority(task.priority),
            implementer = mapTaskImplementer(task.implementer),
            createdAtDate = mapLocalDate(task.createdAt, dateFormatter),
            finishAtDate = mapLocalDate(task.finishAt, dateFormatter),
            updatedAtDate = mapLocalDate(task.updatedAt, dateFormatter)
        )
    }

    private fun mapTaskStatus(taskStatus: TaskStatusDto): TaskStatus {
        return when (taskStatus) {
            TaskStatusDto.TO_DO -> TaskStatus.TODO
            TaskStatusDto.IN_PROGRESS -> TaskStatus.IN_PROGRESS
            TaskStatusDto.COMPLETED -> TaskStatus.COMPLETED
            TaskStatusDto.CANCELLED -> TaskStatus.CANCELLED
        }
    }

    private fun mapTaskPriority(taskPriority: TaskPriorityDto): TaskPriority {
        return when (taskPriority) {
            TaskPriorityDto.HIGH -> TaskPriority.HIGH
            TaskPriorityDto.MEDIUM -> TaskPriority.MEDIUM
            TaskPriorityDto.LOW -> TaskPriority.LOW
        }
    }

    private fun mapTaskImplementer(taskImplementer: TaskImplementerDto?): TaskImplementer {
        return when (taskImplementer) {
            TaskImplementerDto.USER -> TaskImplementer.USER
            TaskImplementerDto.PURCHASES_MANAGER -> TaskImplementer.PURCHASES_MANAGER
            TaskImplementerDto.STOREKEEPER -> TaskImplementer.STOREKEEPER
            null -> TaskImplementer.UNKNOWN
        }
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