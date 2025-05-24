package com.ruparts.app.features.task.data.repository

import com.google.gson.Gson
import com.ruparts.app.core.data.network.EndpointRetrofitService
import com.ruparts.app.core.data.network.request
import com.ruparts.app.features.task.data.network.model.TaskUpdateRequestDataDto
import com.ruparts.app.features.task.data.network.model.TaskUpdateRequestDto
import com.ruparts.app.features.task.data.network.model.TaskUpdateResponseDto
import com.ruparts.app.features.taskslist.model.TaskListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val endpointService: EndpointRetrofitService,
    private val gson: Gson,
) {
    suspend fun updateTask(task: TaskListItem) = withContext(Dispatchers.IO) {
        val response = endpointService.request<TaskUpdateRequestDto, TaskUpdateResponseDto>(
            body = TaskUpdateRequestDto(
                data = TaskUpdateRequestDataDto.fromTaskListItem(task),
            ),
            gson = gson,
        )
        return@withContext response.data
    }
}