package com.ruparts.app.features.task.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.core.utils.runCoroutineCatching
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
                mapper.mapTask(response.data)
            }
        }

    suspend fun changeTaskStatus(id: Long, newStatus: TaskStatus): Result<TaskListItem> =
        withContext(Dispatchers.IO) {
            runCoroutineCatching {
                // TODO: здесь зделать запрос на обновление статуса
                // endpointService.request<TaskChangeStatusRequestDto, TaskChangeStatusResponseDto>(...)
                // ...
            }
            Result.failure(Throwable()) // TODO: эту строку удалить (сейчас нужна, чтобы компилировался код)
        }
}