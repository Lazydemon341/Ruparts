package com.ruparts.app.features.taskslist.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.features.taskslist.data.network.model.TaskListRequestDataDto
import com.ruparts.app.features.taskslist.data.network.model.TaskListRequestDto
import com.ruparts.app.features.taskslist.data.network.model.TaskListResponseDto
import com.ruparts.app.features.taskslist.data.network.model.TaskPriorityDto
import com.ruparts.app.features.taskslist.data.network.model.TaskStatusDto
import com.ruparts.app.features.taskslist.model.TaskListGroup
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskPriority
import com.ruparts.app.features.taskslist.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskListRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
) {

    suspend fun getTaskList(): List<TaskListGroup> = withContext(Dispatchers.Default) {
        val response =
            endpointService.request<TaskListRequestDto, TaskListResponseDto>(
                body = TaskListRequestDto(
                    data = TaskListRequestDataDto(),
                ),
                gson = gson,
            )
        return@withContext response.data.list
            .groupBy { it.type }
            .map { (type, tasks) ->
                TaskListGroup(
                    title = type,
                    tasks = tasks.map { task ->
                        TaskListItem(
                            id = task.id,
                            title = task.title,
                            description = task.description,
                            status = toDomain(task.status),
                            priority = toDomain(task.priority),
                            date = "", // TODO
                            implementer = task.implementer,
                            finishAtDate = task.finishAt,
                        )
                    }
                )
            }
    }

    private fun toDomain(taskStatus: TaskStatusDto): TaskStatus {
        return when (taskStatus) {
            TaskStatusDto.TO_DO -> TaskStatus.TODO
            TaskStatusDto.IN_PROGRESS -> TaskStatus.IN_PROGRESS
            TaskStatusDto.COMPLETED -> TaskStatus.COMPLETED
            TaskStatusDto.CANCELLED -> TaskStatus.CANCELLED
        }
    }

    private fun toDomain(taskPriority: TaskPriorityDto): TaskPriority {
        return when (taskPriority) {
            TaskPriorityDto.HIGH -> TaskPriority.HIGH
            TaskPriorityDto.MEDIUM -> TaskPriority.MEDIUM
            TaskPriorityDto.LOW -> TaskPriority.LOW
        }
    }
}