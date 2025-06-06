package com.ruparts.app.features.task.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
import com.ruparts.app.features.task.data.network.model.ErrorResponseDto
import com.ruparts.app.features.task.data.network.model.TaskChangeStatusRequestDataDto
import com.ruparts.app.features.task.data.network.model.TaskChangeStatusRequestDto
import com.ruparts.app.features.task.data.network.model.TaskChangeStatusResponseDto
import com.ruparts.app.features.task.data.network.model.TaskUpdateRequestDataDto
import com.ruparts.app.features.task.data.network.model.TaskUpdateRequestDto
import com.ruparts.app.features.task.data.network.model.TaskUpdateResponseDto
import com.ruparts.app.features.taskslist.data.mapper.TaskMapper
import com.ruparts.app.features.taskslist.model.TaskListItem
import com.ruparts.app.features.taskslist.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
    private val mapper: TaskMapper,
) {
    suspend fun updateTask(task: TaskListItem): Result<TaskListItem> =
        withContext(Dispatchers.IO) {
            runCoroutineCatching {
                val response = endpointService.request<TaskUpdateRequestDto, TaskUpdateResponseDto>(
                    body = TaskUpdateRequestDto(
                        data = TaskUpdateRequestDataDto.fromTask(task),
                    ),
                    gson = gson,
                )

                when (response.type) {
                    0 -> {
                        response.data?.let { mapper.mapTask(it) }
                            ?: throw IllegalStateException("Success response with null data")
                    }

                    1 -> {
                        handleError(response.error)
                    }

                    else -> throw IllegalStateException("Unknown response type: ${response.type}")
                }
            }
        }

    suspend fun changeTaskStatus(id: Long, newStatus: TaskStatus): Result<TaskListItem> =
        withContext(Dispatchers.IO) {
            runCoroutineCatching {
                val response =
                    endpointService.request<TaskChangeStatusRequestDto, TaskChangeStatusResponseDto>(
                        body = TaskChangeStatusRequestDto(
                            data = TaskChangeStatusRequestDataDto.fromTask(id, newStatus),
                        ),
                        gson = gson,
                    )

                when (response.type) {
                    0 -> {
                        response.data?.let { mapper.mapTask(it) }
                            ?: throw IllegalStateException("Success response with null data")
                    }

                    1 -> {
                        handleError(response.error)
                    }

                    else -> throw IllegalStateException("Unknown response type: ${response.type}")
                }
            }
        }

    private fun handleError(error: ErrorResponseDto?): Nothing {
        val errorMessages = error?.data
            ?.associateBy({ it.path }, { it.title })
            ?: emptyMap()
        throw TaskUpdateException(errorMessages)
    }
}

class TaskUpdateException(val errorMessages: Map<String, String>) : Exception()
